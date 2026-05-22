package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.CardRank;
import com.breixo.culo.domain.model.cards.enums.Suit;
import com.breixo.culo.domain.model.game.Round;
import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import com.breixo.culo.domain.port.input.game.RoundService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

/** The Class Turn Management Service Impl Test. */
@ExtendWith(MockitoExtension.class)
class TurnManagementServiceImplTest {

    /** The turn management service. */
    @InjectMocks
    TurnManagementServiceImpl turnManagementService;

    /** The round service. */
    @Mock
    RoundService roundService;

    /** Test finish round and set opener when last player is out then opens with next active player. */
    @Test
    void testFinishRoundAndSetOpener_whenLastPlayerIsOut_thenOpensWithNextActivePlayer() {
        // Given
        final var player = Instancio.of(Player.class).set(field(Player::id), "p1").create();
        final var playerOne = Instancio.of(Player.class).set(field(Player::id), "p2").create();
        final var playerTwo = Instancio.of(Player.class).set(field(Player::id), "p3").create();
        final var playerThree = Instancio.of(Player.class).set(field(Player::id), "p4").create();

        final Map<String, List<Card>> hands = new HashMap<>();
        hands.put("p1", new ArrayList<>(List.of(Card.builder().suit(Suit.OROS).number(4).build())));
        hands.put("p2", new ArrayList<>());
        hands.put("p3", new ArrayList<>(List.of(
                Card.builder().suit(Suit.OROS).number(5).build(),
                Card.builder().suit(Suit.COPAS).number(6).build())));
        hands.put("p4", new ArrayList<>(List.of(Card.builder().suit(Suit.OROS).number(7).build())));

        final var currentRound = Round.builder()
                .requirement(2)
                .lastRank(CardRank.SIETE)
                .lastCardNumber(4)
                .lastPlayerId("p2")
                .playersPassedSinceLastPlay(Set.of())
                .lastPlayedCards(List.of())
                .build();
        final var resetRound = Round.builder()
                .requirement(0)
                .lastRank(null)
                .lastCardNumber(0)
                .playersPassedSinceLastPlay(Set.of())
                .lastPlayedCards(List.of())
                .build();
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::hands), hands)
                .set(field(GameSession::playerOrder), List.of("p1", "p2", "p3", "p4"))
                .set(field(GameSession::currentPlayerIndex), 1)
                .set(field(GameSession::currentRound), currentRound)
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(player, playerOne, playerTwo, playerThree))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .set(field(Room::gameSession), gameSession)
                .create();

        when(this.roundService.reset(currentRound)).thenReturn(resetRound);

        // When
        final var updatedRoom = this.turnManagementService.finishRoundAndSetOpener(room);

        // Then
        final var currentPlayerId = updatedRoom.gameSession().playerOrder()
                .get(updatedRoom.gameSession().currentPlayerIndex());
        assertEquals("p3", currentPlayerId);
        assertNull(updatedRoom.gameSession().currentRound().lastRank());
    }
}
