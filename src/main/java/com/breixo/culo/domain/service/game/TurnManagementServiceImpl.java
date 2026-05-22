package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.constants.TurnConstants;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.game.RoundService;
import com.breixo.culo.domain.port.input.game.TurnManagementService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * The Class TurnManagementServiceImpl.
 */
@Service
@RequiredArgsConstructor
public class TurnManagementServiceImpl implements TurnManagementService {

    /** The round service. */
    private final RoundService roundService;

    /** {@inheritDoc} */
    @Override
    public String getNextActivePlayerId(final Room room) {

        final var playerCount = room.gameSession().playerOrder().size();

        for (var step = TurnConstants.SINGLE_ADVANCE_STEP; step <= playerCount; step++) {
            final var nextIndex = (room.gameSession().currentPlayerIndex() + step) % playerCount;
            final var playerId = room.gameSession().playerOrder().get(nextIndex);
            final var hand = room.gameSession().hands().get(playerId);

            if (this.isActiveHand(hand)) {
                return playerId;
            }
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Room advanceTurn(final Room room, final boolean skipOne) {

        var roomAfterAdvance = room;
        final var playerCount = room.gameSession().playerOrder().size();
        final var steps = skipOne ? TurnConstants.PLIN_SKIP_STEP : TurnConstants.SINGLE_ADVANCE_STEP;

        for (var stepIndex = TurnConstants.FIRST_PLAYER_INDEX; stepIndex < steps; stepIndex++) {
            roomAfterAdvance = this.advanceOneStep(roomAfterAdvance, playerCount);
            roomAfterAdvance = this.ensureCurrentPlayerIsActive(roomAfterAdvance);
        }

        return roomAfterAdvance;
    }

    /** {@inheritDoc} */
    @Override
    public Integer findFirstActivePlayerIndex(final Room room) {

        final var playerCount = room.gameSession().playerOrder().size();

        for (var playerIndex = TurnConstants.FIRST_PLAYER_INDEX; playerIndex < playerCount; playerIndex++) {
            final var playerId = room.gameSession().playerOrder().get(playerIndex);
            final var hand = room.gameSession().hands().get(playerId);

            if (this.isActiveHand(hand)) {
                return playerIndex;
            }
        }

        return room.gameSession().currentPlayerIndex();
    }

    /** {@inheritDoc} */
    @Override
    public Integer findNextActivePlayerIndexAfter(final Room room, final String playerId) {

        final var startIndex = room.gameSession().playerOrder().indexOf(playerId);

        if (startIndex < TurnConstants.FIRST_PLAYER_INDEX) {
            return this.findFirstActivePlayerIndex(room);
        }

        final var playerCount = room.gameSession().playerOrder().size();

        for (var step = TurnConstants.SINGLE_ADVANCE_STEP; step <= playerCount; step++) {
            final var playerIndex = (startIndex + step) % playerCount;
            final var nextPlayerId = room.gameSession().playerOrder().get(playerIndex);
            final var hand = room.gameSession().hands().get(nextPlayerId);

            if (this.isActiveHand(hand)) {
                return playerIndex;
            }
        }

        return room.gameSession().currentPlayerIndex();
    }

    /** {@inheritDoc} */
    @Override
    public Room ensureCurrentPlayerIsActive(final Room room) {

        var roomAfterEnsure = room;
        final var playerCount = room.gameSession().playerOrder().size();
        var safety = TurnConstants.FIRST_PLAYER_INDEX;

        while (safety < playerCount) {
            final var currentPlayerId = roomAfterEnsure.gameSession().playerOrder()
                    .get(roomAfterEnsure.gameSession().currentPlayerIndex());
            final var currentPlayerHand = roomAfterEnsure.gameSession().hands().get(currentPlayerId);

            if (this.isActiveHand(currentPlayerHand)) {
                break;
            }

            roomAfterEnsure = this.advanceOneStep(roomAfterEnsure, playerCount);
            safety++;
        }

        return roomAfterEnsure;
    }

    /** {@inheritDoc} */
    @Override
    public Room finishRoundAndSetOpener(final Room room) {

        final var lastPlayerId = room.gameSession().currentRound().lastPlayerId();
        final var roomAfterReset = this.resetRoundState(room);

        if (Objects.isNull(lastPlayerId)) {
            return this.ensureCurrentPlayerIsActive(roomAfterReset);
        }

        final var openerIndex = this.resolveOpenerIndex(roomAfterReset, lastPlayerId);
        final var roomWithOpener = this.applyOpenerIndex(roomAfterReset, openerIndex);

        return this.ensureCurrentPlayerIsActive(roomWithOpener);
    }

    /**
	 * Checks if is active hand.
	 *
	 * @param hand the hand
	 * @return true, if is active hand
	 */
    private boolean isActiveHand(final List<com.breixo.culo.domain.model.cards.Card> hand) {

        return Objects.nonNull(hand) && BooleanUtils.isFalse(hand.isEmpty());
    }

    /**
	 * Advance one step.
	 *
	 * @param room        the room
	 * @param playerCount the player count
	 * @return the room
	 */
    private Room advanceOneStep(final Room room, final int playerCount) {

        return room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .currentPlayerIndex((room.gameSession().currentPlayerIndex() + 1) % playerCount)
                        .build())
                .build();
    }

    /**
	 * Reset round state.
	 *
	 * @param room the room
	 * @return the room
	 */
    private Room resetRoundState(final Room room) {

        final var resetRound = this.roundService.reset(room.gameSession().currentRound());

        return room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .currentRound(resetRound)
                        .build())
                .build();
    }

    /**
	 * Resolve opener index.
	 *
	 * @param room         the room
	 * @param lastPlayerId the last player id
	 * @return the integer
	 */
    private Integer resolveOpenerIndex(final Room room, final String lastPlayerId) {

        final var lastPlayerHand = room.gameSession().hands().get(lastPlayerId);
        final var lastPlayerOut = BooleanUtils.isFalse(this.isActiveHand(lastPlayerHand));

        if (lastPlayerOut) {
            return this.findNextActivePlayerIndexAfter(room, lastPlayerId);
        }

        return room.gameSession().playerOrder().indexOf(lastPlayerId);
    }

    /**
	 * Apply opener index.
	 *
	 * @param room        the room
	 * @param openerIndex the opener index
	 * @return the room
	 */
    private Room applyOpenerIndex(final Room room, final Integer openerIndex) {

        if (openerIndex < TurnConstants.FIRST_PLAYER_INDEX) {
            return room;
        }

        return room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .currentPlayerIndex(openerIndex)
                        .build())
                .build();
    }
}
