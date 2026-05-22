package com.breixo.culo.domain.model.room;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.game.Round;
import com.breixo.culo.domain.model.cards.QuadDiscardEvent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Record GameSession.
 *
 * @param hands               the hands
 * @param playerOrder         the player order
 * @param currentPlayerIndex  the current player index
 * @param currentRound        the current round
 * @param playEpoch           the play epoch
 * @param lastCuloId          the last culo id
 * @param finishOrder         the finish order
 * @param pendingQuadDiscards the pending quad discards
 */
@Builder(toBuilder = true)
public record GameSession(
        @NotNull Map<String, List<Card>> hands,
        @NotNull List<String> playerOrder,
        Integer currentPlayerIndex,
        @NotNull Round currentRound,
        Integer playEpoch,
        String lastCuloId,
        @NotNull List<String> finishOrder,
        @NotNull List<QuadDiscardEvent> pendingQuadDiscards
) {

    /**
	 * Instantiates a new game session.
	 *
	 * @param hands               the hands
	 * @param playerOrder         the player order
	 * @param currentPlayerIndex  the current player index
	 * @param currentRound        the current round
	 * @param playEpoch           the play epoch
	 * @param lastCuloId          the last culo id
	 * @param finishOrder         the finish order
	 * @param pendingQuadDiscards the pending quad discards
	 */
    public GameSession {
        hands = Map.copyOf(hands.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> List.copyOf(entry.getValue()))));
        playerOrder = List.copyOf(playerOrder);
        finishOrder = List.copyOf(finishOrder);
        pendingQuadDiscards = List.copyOf(pendingQuadDiscards);
    }
}
