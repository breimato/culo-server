package com.breixo.culo.domain.service.cards;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.CardRank;
import com.breixo.culo.domain.model.cards.enums.Suit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** The Class Card Rank Resolver Service Impl Test. */
@ExtendWith(MockitoExtension.class)
class CardRankResolverServiceImplTest {

    /** The card rank resolver service. */
    @InjectMocks
    CardRankResolverServiceImpl cardRankResolverService;

    /** The card factory service. */
    @InjectMocks
    CardFactoryServiceImpl cardFactoryService;

    /** Test resolve when as oros then return as oros rank. */
    @Test
    void testResolve_whenAsOros_thenReturnAsOrosRank() {
        // Given
        final var card = this.cardFactoryService.buildCard(Suit.OROS, 1);

        // When
        final var cardRank = this.cardRankResolverService.resolve(card);

        // Then
        assertEquals(CardRank.AS_OROS, cardRank);
    }

    /** Test resolve when as not oros then return as otro rank. */
    @Test
    void testResolve_whenAsNotOros_thenReturnAsOtroRank() {
        // Given
        final var card = this.cardFactoryService.buildCard(Suit.COPAS, 1);

        // When
        final var cardRank = this.cardRankResolverService.resolve(card);

        // Then
        assertEquals(CardRank.AS_OTRO, cardRank);
    }

    /** Test resolve when number is seven then return siete rank. */
    @Test
    void testResolve_whenNumberIsSeven_thenReturnSieteRank() {
        // Given
        final var card = this.cardFactoryService.buildCard(Suit.ESPADAS, 7);

        // When
        final var cardRank = this.cardRankResolverService.resolve(card);

        // Then
        assertEquals(CardRank.SIETE, cardRank);
    }

    /** Test resolve when invalid number then throw game exception. */
    @Test
    void testResolve_whenInvalidNumber_thenThrowGameException() {
        // Given
        final var card = Card.builder().suit(Suit.BASTOS).number(8).build();

        // When
        final var gameException = assertThrows(
                GameException.class,
                () -> this.cardRankResolverService.resolve(card));

        // Then
        assertEquals(GameExceptionConstants.INVALID_CARD_NUMBER, gameException.getMessage());
    }
}
