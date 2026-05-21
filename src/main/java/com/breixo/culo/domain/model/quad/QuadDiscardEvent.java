package com.breixo.culo.domain.model.quad;

import com.breixo.culo.domain.model.card.Card;
import lombok.Builder;

import java.util.List;

/** The Record QuadDiscardEvent. */
@Builder
public record QuadDiscardEvent(
        String playerId,
        Integer value,
        List<Card> cards
) {

    public QuadDiscardEvent {
        cards = List.copyOf(cards);
    }
}
