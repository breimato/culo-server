package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.command.cards.CardInput;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.Suit;
import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.cards.CardFactoryService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class PlayBuilderServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class PlayBuilderServiceImplTest {

    /** The play builder service. */
    @InjectMocks
    PlayBuilderServiceImpl playBuilderService;

    /** The card factory service. */
    @Mock
    CardFactoryService cardFactoryService;

    /**
	 * Test build play when cards share same number then return play.
	 */
    @Test
    void testBuildPlay_whenCardsShareSameNumber_thenReturnPlay() {
        
        // Given
        final var card = Card.builder().suit(Suit.OROS).number(7).build();
        final var cardOne = Card.builder().suit(Suit.COPAS).number(7).build();
        final var cards = List.of(card, cardOne);

        // When
        final var play = this.playBuilderService.buildPlay(cards);

        // Then
        assertEquals(cards, play.cards());
    }

    /**
	 * Test build play when cards have different numbers then throw game exception.
	 */
    @Test
    void testBuildPlay_whenCardsHaveDifferentNumbers_thenThrowGameException() {
        
        // Given
        final var card = Card.builder().suit(Suit.OROS).number(7).build();
        final var cardOne = Card.builder().suit(Suit.COPAS).number(10).build();
        final var cards = List.of(card, cardOne);

        // When
        final var gameException = assertThrows(
                GameException.class,
                () -> this.playBuilderService.buildPlay(cards));

        // Then
        assertEquals(GameExceptionConstants.INVALID_PLAY, gameException.getMessage());
    }

    /**
	 * Test to cards when cards not in hand then throw game exception.
	 */
    @Test
    void testToCards_whenCardsNotInHand_thenThrowGameException() {
        
        // Given
        final var playerId = Instancio.create(String.class);
        final var cardInput = Instancio.of(CardInput.class)
                .set(field(CardInput::suit), Suit.OROS)
                .set(field(CardInput::number), 7)
                .create();
        final var card = Card.builder().suit(Suit.OROS).number(7).build();
        final Map<String, List<Card>> hands = new HashMap<>();
        hands.put(playerId, List.of());
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::hands), hands)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();

        // When
        when(this.cardFactoryService.buildCard(cardInput.suit(), cardInput.number())).thenReturn(card);
        final var gameException = assertThrows(
                GameException.class,
                () -> this.playBuilderService.toCards(List.of(cardInput), room, playerId));

        // Then
        verify(this.cardFactoryService, times(1)).buildCard(cardInput.suit(), cardInput.number());
        assertEquals(GameExceptionConstants.CARDS_NOT_IN_HAND, gameException.getMessage());
    }
}
