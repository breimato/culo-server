package com.breixo.culo.domain.model.cards;

import com.breixo.culo.domain.model.cards.Card;
import lombok.Builder;

import java.util.List;

/** The Record QuadInHand. */
@Builder
public record QuadInHand(
        List<Card> updatedHand,
        List<QuadDiscardEvent> events
) {
}
