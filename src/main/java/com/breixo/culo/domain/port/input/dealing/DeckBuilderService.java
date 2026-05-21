package com.breixo.culo.domain.port.input.dealing;

import com.breixo.culo.domain.model.card.Card;

import java.util.List;

/** The Interface DeckBuilderService. */
public interface DeckBuilderService {

    /**
     * Build shuffled deck.
     *
     * @return the list
     */
    List<Card> buildShuffledDeck();
}
