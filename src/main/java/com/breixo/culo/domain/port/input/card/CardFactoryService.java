package com.breixo.culo.domain.port.input.card;

import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.card.enums.Suit;

/** The Interface CardFactoryService. */
public interface CardFactoryService {

    /**
     * Build card.
     *
     * @param suit   the suit
     * @param number the number
     * @return the card
     */
    Card buildCard(Suit suit, Integer number);
}
