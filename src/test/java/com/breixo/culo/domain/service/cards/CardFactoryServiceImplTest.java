package com.breixo.culo.domain.service.cards;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.cards.enums.Suit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The Class CardFactoryServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class CardFactoryServiceImplTest {

    /** The card factory service. */
    @InjectMocks
    CardFactoryServiceImpl cardFactoryService;

    /**
	 * Test build card when suit and number are valid then return card.
	 */
    @Test
    void testBuildCard_whenSuitAndNumberAreValid_thenReturnCard() {
        
        // Given
        final var suit = Suit.OROS;
        final var number = 1;

        // When
        final var card = this.cardFactoryService.buildCard(suit, number);

        // Then
        assertEquals(suit, card.suit());
        assertEquals(number, card.number());
    }

    /**
	 * Test build card when number is invalid then throw game exception.
	 */
    @Test
    void testBuildCard_whenNumberIsInvalid_thenThrowGameException() {
        
        // Given
        final var suit = Suit.BASTOS;
        final var number = 8;

        // When
        final var gameException = assertThrows(
                GameException.class,
                () -> this.cardFactoryService.buildCard(suit, number));

        // Then
        assertEquals(GameExceptionConstants.INVALID_CARD_NUMBER, gameException.getMessage());
    }
}
