package com.breixo.culo.domain.model.cards;

import com.breixo.culo.domain.model.cards.Card;
import lombok.Builder;

import java.util.List;

/**
 * The Record QuadInHand.
 *
 * @param updatedHand the updated hand
 * @param events      the events
 */
@Builder
public record QuadInHand(
        List<Card> updatedHand,
        List<QuadDiscardEvent> events
) {
}
