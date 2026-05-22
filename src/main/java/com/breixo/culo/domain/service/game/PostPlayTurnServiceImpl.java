package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.model.game.PlayerEliminationCheck;
import com.breixo.culo.domain.model.game.PlayTraits;
import com.breixo.culo.domain.model.game.TurnAfterPlay;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.game.PostPlayTurnService;
import com.breixo.culo.domain.port.input.player.PlayerEliminationService;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.input.game.RoundClosingService;
import com.breixo.culo.domain.port.input.game.TurnManagementService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

/**
 * The Class PostPlayTurnServiceImpl.
 */
@Service
@RequiredArgsConstructor
public class PostPlayTurnServiceImpl implements PostPlayTurnService {

    /** The game session context service. */
    private final GameContextService gameSessionContextService;

    /** The player outcome service. */
    private final PlayerEliminationService playerOutcomeService;

    /** The room phase service. */
    private final RoomPhaseService roomPhaseService;

    /** The turn management service. */
    private final TurnManagementService turnManagementService;

    /** The round close service. */
    private final RoundClosingService roundCloseService;

    /** {@inheritDoc} */
    @Override
    public TurnAfterPlay apply(final Room room, final Player player, final PlayTraits playFlags) {

        final var afterPlayerOut = this.registerPlayerOutIfNeeded(room, player);

        if (afterPlayerOut.gameFinished()) {
            return this.outcomeWhenGameEnded(afterPlayerOut.room());
        }

        if (playFlags.isAsOros()) {
            return this.outcomeWhenAsOros(afterPlayerOut.room(), afterPlayerOut.playerWasEliminated());
        }

        return this.outcomeWhenNormalPlay(afterPlayerOut.room(), playFlags.plin());
    }

    /**
	 * Register player out if needed.
	 *
	 * @param room   the room
	 * @param player the player
	 * @return the player elimination check
	 */
    private PlayerEliminationCheck registerPlayerOutIfNeeded(final Room room, final Player player) {

        final var playerId = player.id();
        final var playerOut = this.gameSessionContextService.isPlayerOut(room, playerId);

        if (BooleanUtils.isFalse(playerOut)) {
            return PlayerEliminationCheck.builder()
                    .room(room)
                    .playerWasEliminated(false)
                    .gameFinished(false)
                    .build();
        }

        final var registerPlayerOutResult = this.playerOutcomeService.registerPlayerOut(room, playerId);

        return PlayerEliminationCheck.builder()
                .room(registerPlayerOutResult.room())
                .playerWasEliminated(true)
                .gameFinished(registerPlayerOutResult.gameFinished())
                .build();
    }

    /**
	 * Outcome when game ended.
	 *
	 * @param room the room
	 * @return the turn after play
	 */
    private TurnAfterPlay outcomeWhenGameEnded(final Room room) {

        final var roomInDealingPhase = this.roomPhaseService.withPhase(room, GamePhase.DEALING);

        return TurnAfterPlay.builder()
                .room(roomInDealingPhase)
                .gameFinished(true)
                .roundClosedByPlin(false)
                .build();
    }

    /**
	 * Outcome when as oros.
	 *
	 * @param room                the room
	 * @param playerWasEliminated the player was eliminated
	 * @return the turn after play
	 */
    private TurnAfterPlay outcomeWhenAsOros(final Room room, final boolean playerWasEliminated) {

        if (BooleanUtils.isFalse(playerWasEliminated)) {
            return TurnAfterPlay.builder()
                    .room(room)
                    .gameFinished(false)
                    .roundClosedByPlin(false)
                    .build();
        }

        final var roomAfterTurn = this.turnManagementService.advanceTurn(room, false);

        return TurnAfterPlay.builder()
                .room(roomAfterTurn)
                .gameFinished(false)
                .roundClosedByPlin(false)
                .build();
    }

    /**
	 * Outcome when normal play.
	 *
	 * @param room the room
	 * @param plin the plin
	 * @return the turn after play
	 */
    private TurnAfterPlay outcomeWhenNormalPlay(final Room room, final boolean plin) {

        final var roomAfterTurn = this.turnManagementService.advanceTurn(room, plin);

        if (BooleanUtils.isFalse(plin)) {
            return TurnAfterPlay.builder()
                    .room(roomAfterTurn)
                    .gameFinished(false)
                    .roundClosedByPlin(false)
                    .build();
        }

        final var roundCloseResult = this.roundCloseService.closeRoundIfOthersAllPassed(roomAfterTurn);

        return TurnAfterPlay.builder()
                .room(roundCloseResult.room())
                .gameFinished(false)
                .roundClosedByPlin(roundCloseResult.roundClosed())
                .build();
    }
}
