package com.breixo.culo.domain.model.cards;

import com.breixo.culo.domain.model.cards.Card;
import lombok.Builder;

import java.util.List;

/**
 * The Record QuadDiscardEvent.
 *
 * @param playerId the player id
 * @param value    the value
 * @param cards    the cards
 */
@Builder
public record QuadDiscardEvent(
        String playerId,
        Integer value,
        List<Card> cards
) {

    /**
	 * Instantiates a new quad discard event.
	 *
	 * @param playerId the player id
	 * @param value    the value
	 * @param cards    the cards
	 */
    public QuadDiscardEvent {
        cards = List.copyOf(cards);
    }
}
