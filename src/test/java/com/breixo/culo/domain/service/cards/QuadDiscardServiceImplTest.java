package com.breixo.culo.domain.service.cards;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.Suit;
import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The Class Quad Discard Service Impl Test. */
@ExtendWith(MockitoExtension.class)
class QuadDiscardServiceImplTest {

    /** The quad discard service. */
    @InjectMocks
    QuadDiscardServiceImpl quadDiscardService;

    /** Test discard quads when four cards of same number then removes them. */
    @Test
    void testDiscardQuads_whenFourCardsOfSameNumber_thenRemovesThem() {
        // Given
        final var player = Instancio.create(Player.class);
        final Map<String, List<Card>> hands = new HashMap<>();
        hands.put(player.id(), new ArrayList<>(List.of(
                Card.builder().suit(Suit.OROS).number(4).build(),
                Card.builder().suit(Suit.COPAS).number(4).build(),
                Card.builder().suit(Suit.ESPADAS).number(4).build(),
                Card.builder().suit(Suit.BASTOS).number(4).build(),
                Card.builder().suit(Suit.OROS).number(7).build()
        )));
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::hands), hands)
                .set(field(GameSession::playerOrder), List.of(player.id()))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();

        // When
        final var quadDiscardApplied = this.quadDiscardService.discardQuads(room, player.id());

        // Then
        assertEquals(1, quadDiscardApplied.events().size());
        assertEquals(4, quadDiscardApplied.events().getFirst().value());
        assertEquals(4, quadDiscardApplied.events().getFirst().cards().size());
        assertEquals(1, quadDiscardApplied.room().gameSession().hands().get(player.id()).size());
        assertEquals(7, quadDiscardApplied.room().gameSession().hands().get(player.id()).getFirst().number());
    }

    /** Test discard quads when three of a kind then does nothing. */
    @Test
    void testDiscardQuads_whenThreeOfAKind_thenDoesNothing() {
        // Given
        final var player = Instancio.create(Player.class);
        final Map<String, List<Card>> hands = new HashMap<>();
        hands.put(player.id(), new ArrayList<>(List.of(
                Card.builder().suit(Suit.OROS).number(1).build(),
                Card.builder().suit(Suit.COPAS).number(1).build(),
                Card.builder().suit(Suit.ESPADAS).number(1).build()
        )));
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::hands), hands)
                .set(field(GameSession::playerOrder), List.of(player.id()))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();

        // When
        final var quadDiscardApplied = this.quadDiscardService.discardQuads(room, player.id());

        // Then
        assertTrue(quadDiscardApplied.events().isEmpty());
        assertEquals(3, quadDiscardApplied.room().gameSession().hands().get(player.id()).size());
    }
}
