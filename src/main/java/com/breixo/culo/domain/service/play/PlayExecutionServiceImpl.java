package com.breixo.culo.domain.service.play;

import com.breixo.culo.domain.model.play.Play;
import com.breixo.culo.domain.model.play.PlayExecutionResult;
import com.breixo.culo.domain.model.play.Round;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.play.PlayExecutionService;
import com.breixo.culo.domain.port.input.play.PlayRuleService;
import com.breixo.culo.domain.port.input.player.HandManagementService;
import com.breixo.culo.domain.port.input.player.PlayerOutcomeService;
import com.breixo.culo.domain.port.input.quad.QuadDiscardService;
import com.breixo.culo.domain.port.input.round.RoundService;
import com.breixo.culo.domain.port.input.session.GameSessionContextService;
import com.breixo.culo.domain.port.input.turn.RoundCloseService;
import com.breixo.culo.domain.port.input.turn.TurnManagementService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/** The Class PlayExecutionServiceImpl. */
@Service
@RequiredArgsConstructor
public class PlayExecutionServiceImpl implements PlayExecutionService {

    /** The quad discard service. */
    private final QuadDiscardService quadDiscardService;

    /** The hand management service. */
    private final HandManagementService handManagementService;

    /** The play rule service. */
    private final PlayRuleService playRuleService;

    /** The round service. */
    private final RoundService roundService;

    /** The player outcome service. */
    private final PlayerOutcomeService playerOutcomeService;

    /** The turn management service. */
    private final TurnManagementService turnManagementService;

    /** The round close service. */
    private final RoundCloseService roundCloseService;

    /** The game session context service. */
    private final GameSessionContextService gameSessionContextService;

    /** {@inheritDoc} */
    @Override
    public PlayExecutionResult execute(final Room room, final Player player, final Play play) {

        final var discardQuadsResult = this.quadDiscardService.discardQuads(room, player.id());
        var roomAfterQuads = discardQuadsResult.room();

        final var cards = play.cards();
        final var plin = this.playRuleService.isPlin(play, roomAfterQuads.gameSession().currentRound());
        final var isAsOros = this.playRuleService.isAsOros(play);

        roomAfterQuads = this.handManagementService.removeCardsFromHand(roomAfterQuads, player.id(), cards);
        roomAfterQuads = this.quadDiscardService.discardQuads(roomAfterQuads, player.id()).room();

        final var round = roomAfterQuads.gameSession().currentRound();
        final Round updatedRound;
        if (isAsOros) {
            updatedRound = this.roundService.reset(round);
        } else if (plin) {
            final var skippedPlayerId = this.turnManagementService.getNextActivePlayerId(roomAfterQuads);
            updatedRound = this.roundService.registerPlinPlay(round, play, player.id(), skippedPlayerId);
        } else {
            updatedRound = this.roundService.registerPlay(round, play, player.id());
        }

        roomAfterQuads = roomAfterQuads.toBuilder()
                .gameSession(roomAfterQuads.gameSession().toBuilder()
                        .currentRound(updatedRound)
                        .build())
                .build();

        final var playerOut = this.gameSessionContextService.isPlayerOut(roomAfterQuads, player.id());
        var gameEnded = false;
        if (playerOut) {
            final var registerPlayerOutResult = this.playerOutcomeService.registerPlayerOut(roomAfterQuads, player.id());
            roomAfterQuads = registerPlayerOutResult.room();
            gameEnded = registerPlayerOutResult.gameEnded();
        }

        var roundEndedByPlin = false;
        if (gameEnded) {
            roomAfterQuads = roomAfterQuads.toBuilder()
                    .roomLobby(roomAfterQuads.roomLobby().toBuilder()
                            .phase(GamePhase.DEALING)
                            .build())
                    .build();
        } else if (isAsOros) {
            if (playerOut) {
                roomAfterQuads = this.turnManagementService.advanceTurn(roomAfterQuads, false);
            }
        } else {
            roomAfterQuads = this.turnManagementService.advanceTurn(roomAfterQuads, plin);
            if (plin) {
                final var roundCloseResult = this.roundCloseService.closeRoundIfOthersAllPassed(roomAfterQuads);
                roomAfterQuads = roundCloseResult.room();
                roundEndedByPlin = roundCloseResult.roundEnded();
            }
        }

        return PlayExecutionResult.builder()
                .room(roomAfterQuads)
                .plin(BooleanUtils.isTrue(plin) && BooleanUtils.isFalse(isAsOros))
                .roundEnded(isAsOros || roundEndedByPlin)
                .gameEnded(gameEnded)
                .build();
    }
}
