package com.breixo.culo.domain.service.cards;

import com.breixo.culo.domain.constants.DeckConstants;
import com.breixo.culo.domain.model.cards.enums.Suit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The Class DeckBuilderServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class DeckBuilderServiceImplTest {

    /** The card factory service. */
    final CardFactoryServiceImpl cardFactoryService = new CardFactoryServiceImpl();

    /** The deck builder service. */
    final DeckBuilderServiceImpl deckBuilderService = new DeckBuilderServiceImpl(this.cardFactoryService);

    /**
	 * Test build shuffled deck when called then return full deck.
	 */
    @Test
    void testBuildShuffledDeck_whenCalled_thenReturnFullDeck() {
        // When
        final var deck = this.deckBuilderService.buildShuffledDeck();

        // Then
        final var expectedSize = Suit.values().length * DeckConstants.DECK_NUMBERS.size();
        assertEquals(expectedSize, deck.size());
    }
}
