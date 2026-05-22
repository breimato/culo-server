package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.game.PlayTraits;
import com.breixo.culo.domain.model.game.HandAfterPlay;
import com.breixo.culo.domain.model.game.Round;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.game.HandOnPlayService;
import com.breixo.culo.domain.port.input.game.PlayRuleService;
import com.breixo.culo.domain.port.input.player.HandManagementService;
import com.breixo.culo.domain.port.input.cards.QuadDiscardService;
import com.breixo.culo.domain.port.input.game.RoundService;
import com.breixo.culo.domain.port.input.game.TurnManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The Class HandOnPlayServiceImpl.
 */
@Service
@RequiredArgsConstructor
public class HandOnPlayServiceImpl implements HandOnPlayService {

    /** The quad discard service. */
    private final QuadDiscardService quadDiscardService;

    /** The hand management service. */
    private final HandManagementService handManagementService;

    /** The play rule service. */
    private final PlayRuleService playRuleService;

    /** The round service. */
    private final RoundService roundService;

    /** The turn management service. */
    private final TurnManagementService turnManagementService;

    /** {@inheritDoc} */
    @Override
    public HandAfterPlay apply(final Room room, final Player player, final Play play) {

        final var initialQuadsResult = this.quadDiscardService.discardQuads(room, player.id());
        var roomAfterPlay = initialQuadsResult.room();

        final var playFlags = this.resolvePlayTraits(play, roomAfterPlay);
        roomAfterPlay = this.applyHandUpdates(roomAfterPlay, player.id(), play, playFlags);

        return HandAfterPlay.builder()
                .room(roomAfterPlay)
                .playFlags(playFlags)
                .build();
    }

    /**
	 * Resolve play traits.
	 *
	 * @param play the play
	 * @param room the room
	 * @return the play traits
	 */
    private PlayTraits resolvePlayTraits(final Play play, final Room room) {

        final var currentRound = room.gameSession().currentRound();
        final var plin = this.playRuleService.isPlin(play, currentRound);
        final var isAsOros = this.playRuleService.isAsOros(play);

        return PlayTraits.builder()
                .plin(plin)
                .isAsOros(isAsOros)
                .build();
    }

    /**
	 * Apply hand updates.
	 *
	 * @param room      the room
	 * @param playerId  the player id
	 * @param play      the play
	 * @param playFlags the play flags
	 * @return the room
	 */
    private Room applyHandUpdates(
            final Room room,
            final String playerId,
            final Play play,
            final PlayTraits playFlags) {

        final var roomAfterPlay = this.handManagementService.removeCardsFromHand(room, playerId, play.cards());

        final var quadsAfterPlay = this.quadDiscardService.discardQuads(roomAfterPlay, playerId);

        final var updatedRound = this.buildUpdatedRound(
                quadsAfterPlay.room().gameSession().currentRound(), play, playerId, playFlags, roomAfterPlay);

        return quadsAfterPlay.room().toBuilder()
                .gameSession(quadsAfterPlay.room().gameSession().toBuilder()
                        .currentRound(updatedRound)
                        .build())
                .build();
    }

    /**
	 * Builds the updated round.
	 *
	 * @param round     the round
	 * @param play      the play
	 * @param playerId  the player id
	 * @param playFlags the play flags
	 * @param room      the room
	 * @return the round
	 */
    private Round buildUpdatedRound(
            final Round round,
            final Play play,
            final String playerId,
            final PlayTraits playFlags,
            final Room room) {

        if (playFlags.isAsOros()) {
            return this.roundService.reset(round);
        }

        if (playFlags.plin()) {
            final var skippedPlayerId = this.turnManagementService.getNextActivePlayerId(room);
            return this.roundService.registerPlinPlay(round, play, playerId, skippedPlayerId);
        }

        return this.roundService.registerPlay(round, play, playerId);
    }
}
