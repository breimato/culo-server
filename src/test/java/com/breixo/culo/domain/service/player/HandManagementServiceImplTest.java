package com.breixo.culo.domain.service.player;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.Suit;
import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.Room;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The Class Hand Management Service Impl Test. */
@ExtendWith(MockitoExtension.class)
class HandManagementServiceImplTest {

    /** The hand management service. */
    @InjectMocks
    HandManagementServiceImpl handManagementService;

    /** Test remove cards from hand when cards exist then remove them. */
    @Test
    void testRemoveCardsFromHand_whenCardsExist_thenRemoveThem() {
        // Given
        final var playerId = "player-id";
        final var cardToRemove = Card.builder().suit(Suit.COPAS).number(3).build();
        final var cardToKeep = Card.builder().suit(Suit.OROS).number(7).build();
        final var hands = new HashMap<String, List<Card>>();
        hands.put(playerId, List.of(cardToRemove, cardToKeep));
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::hands), hands)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();

        // When
        final var roomAfterRemove = this.handManagementService.removeCardsFromHand(
                room, playerId, List.of(cardToRemove));

        // Then
        final var hand = roomAfterRemove.gameSession().hands().get(playerId);
        assertEquals(1, hand.size());
        assertEquals(cardToKeep, hand.getFirst());
    }

    /** Test add cards to hand when called then append cards. */
    @Test
    void testAddCardsToHand_whenCalled_thenAppendCards() {
        // Given
        final var playerId = "player-id";
        final var existingCard = Card.builder().suit(Suit.BASTOS).number(5).build();
        final var newCard = Card.builder().suit(Suit.ESPADAS).number(10).build();
        final var hands = new HashMap<String, List<Card>>();
        hands.put(playerId, List.of(existingCard));
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::hands), Map.copyOf(hands))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();

        // When
        final var roomAfterAdd = this.handManagementService.addCardsToHand(room, playerId, List.of(newCard));

        // Then
        final var hand = roomAfterAdd.gameSession().hands().get(playerId);
        assertEquals(2, hand.size());
        assertTrue(hand.contains(existingCard));
        assertTrue(hand.contains(newCard));
    }
}
