package com.breixo.culo.domain.service.turn;

import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.round.RoundService;
import com.breixo.culo.domain.port.input.turn.TurnManagementService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/** The Class TurnManagementServiceImpl. */
@Service
@RequiredArgsConstructor
public class TurnManagementServiceImpl implements TurnManagementService {

    /** The round service. */
    private final RoundService roundService;

    /** {@inheritDoc} */
    @Override
    public String getNextActivePlayerId(final Room room) {

        final int playerCount = room.gameSession().playerOrder().size();

        for (int step = 1; step <= playerCount; step++) {
            final var nextIndex = (room.gameSession().currentPlayerIndex() + step) % playerCount;
            final var playerId = room.gameSession().playerOrder().get(nextIndex);
            final var hand = room.gameSession().hands().get(playerId);

            if (Objects.nonNull(hand) && BooleanUtils.isFalse(hand.isEmpty())) {
                return playerId;
            }
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Room advanceTurn(final Room room, final boolean skipOne) {

        var roomAfterAdvance = room;
        final int playerCount = room.gameSession().playerOrder().size();
        final int steps = skipOne ? 2 : 1;

        for (int stepIndex = 0; stepIndex < steps; stepIndex++) {
            roomAfterAdvance = roomAfterAdvance.toBuilder()
                    .gameSession(roomAfterAdvance.gameSession().toBuilder()
                            .currentPlayerIndex((roomAfterAdvance.gameSession().currentPlayerIndex() + 1) % playerCount)
                            .build())
                    .build();
            roomAfterAdvance = this.ensureCurrentPlayerIsActive(roomAfterAdvance);
        }

        return roomAfterAdvance;
    }

    /** {@inheritDoc} */
    @Override
    public Integer findFirstActivePlayerIndex(final Room room) {

        for (Integer playerIndex = 0; Integer.compare(playerIndex, room.gameSession().playerOrder().size()) < 0; playerIndex++) {
            final var playerId = room.gameSession().playerOrder().get(playerIndex);
            final var hand = room.gameSession().hands().get(playerId);

            if (Objects.nonNull(hand) && BooleanUtils.isFalse(hand.isEmpty())) {
                return playerIndex;
            }
        }

        return room.gameSession().currentPlayerIndex();
    }

    /** {@inheritDoc} */
    @Override
    public Integer findNextActivePlayerIndexAfter(final Room room, final String playerId) {

        final Integer startIndex = room.gameSession().playerOrder().indexOf(playerId);

        if (Integer.compare(startIndex, 0) < 0) {
            return this.findFirstActivePlayerIndex(room);
        }

        final Integer playerCount = room.gameSession().playerOrder().size();

        for (Integer step = 1; Integer.compare(step, playerCount) <= 0; step++) {
            final Integer playerIndex = (startIndex + step) % playerCount;
            final var nextPlayerId = room.gameSession().playerOrder().get(playerIndex);
            final var hand = room.gameSession().hands().get(nextPlayerId);

            if (Objects.nonNull(hand) && BooleanUtils.isFalse(hand.isEmpty())) {
                return playerIndex;
            }
        }

        return room.gameSession().currentPlayerIndex();
    }

    /** {@inheritDoc} */
    @Override
    public Room ensureCurrentPlayerIsActive(final Room room) {

        var roomAfterEnsure = room;
        final int playerCount = room.gameSession().playerOrder().size();
        int safety = 0;

        while (safety < playerCount) {
            final var currentPlayerId = roomAfterEnsure.gameSession().playerOrder()
                    .get(roomAfterEnsure.gameSession().currentPlayerIndex());
            final var currentPlayerHand = roomAfterEnsure.gameSession().hands().get(currentPlayerId);
            final var currentPlayerOut = Objects.isNull(currentPlayerHand) || currentPlayerHand.isEmpty();

            if (BooleanUtils.isFalse(currentPlayerOut)) {
                break;
            }

            roomAfterEnsure = roomAfterEnsure.toBuilder()
                    .gameSession(roomAfterEnsure.gameSession().toBuilder()
                            .currentPlayerIndex((roomAfterEnsure.gameSession().currentPlayerIndex() + 1) % playerCount)
                            .build())
                    .build();
            safety++;
        }

        return roomAfterEnsure;
    }

    /** {@inheritDoc} */
    @Override
    public Room finishRoundAndSetOpener(final Room room) {

        final var lastPlayerId = room.gameSession().currentRound().lastPlayerId();
        final var resetRound = this.roundService.reset(room.gameSession().currentRound());
        var roomAfterReset = room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .currentRound(resetRound)
                        .build())
                .build();

        if (Objects.isNull(lastPlayerId)) {
            return this.ensureCurrentPlayerIsActive(roomAfterReset);
        }

        final var lastPlayerHand = roomAfterReset.gameSession().hands().get(lastPlayerId);
        final var lastPlayerOut = Objects.isNull(lastPlayerHand) || lastPlayerHand.isEmpty();
        final Integer openerIndex = lastPlayerOut
                ? this.findNextActivePlayerIndexAfter(roomAfterReset, lastPlayerId)
                : roomAfterReset.gameSession().playerOrder().indexOf(lastPlayerId);

        if (Integer.compare(openerIndex, 0) >= 0) {
            roomAfterReset = roomAfterReset.toBuilder()
                    .gameSession(roomAfterReset.gameSession().toBuilder()
                            .currentPlayerIndex(openerIndex)
                            .build())
                    .build();
        }

        return this.ensureCurrentPlayerIsActive(roomAfterReset);
    }
}
