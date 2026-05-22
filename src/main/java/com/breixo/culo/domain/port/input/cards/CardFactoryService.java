package com.breixo.culo.domain.port.input.cards;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.Suit;

/**
 * The Interface CardFactoryService.
 */
public interface CardFactoryService {

    /**
	 * Builds the card.
	 *
	 * @param suit   the suit
	 * @param number the number
	 * @return the card
	 */
    Card buildCard(Suit suit, Integer number);
}
