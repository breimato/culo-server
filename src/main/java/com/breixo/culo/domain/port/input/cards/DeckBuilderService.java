package com.breixo.culo.domain.port.input.cards;

import com.breixo.culo.domain.model.cards.Card;

import java.util.List;

/**
 * The Interface DeckBuilderService.
 */
public interface DeckBuilderService {

    /**
	 * Builds the shuffled deck.
	 *
	 * @return the list
	 */
    List<Card> buildShuffledDeck();
}
