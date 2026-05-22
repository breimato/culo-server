package com.breixo.culo.domain.model.cards;

import com.breixo.culo.domain.model.cards.Card;
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
