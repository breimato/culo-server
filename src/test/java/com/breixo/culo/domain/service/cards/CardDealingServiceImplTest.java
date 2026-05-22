package com.breixo.culo.domain.service.cards;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.Suit;
import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.cards.CardFactoryService;
import com.breixo.culo.domain.port.input.cards.DeckBuilderService;
import com.breixo.culo.domain.port.input.player.HandManagementService;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The Class Card Dealing Service Impl Test. */
@ExtendWith(MockitoExtension.class)
class CardDealingServiceImplTest {

    /** The card dealing service. */
    @InjectMocks
    CardDealingServiceImpl cardDealingService;

    /** The deck builder service. */
    @Mock
    DeckBuilderService deckBuilderService;

    /** The card factory service. */
    @Mock
    CardFactoryService cardFactoryService;

    /** The hand management service. */
    @Mock
    HandManagementService handManagementService;

    /** Test find two of oros player index when second player has two of oros then return index one. */
    @Test
    void testFindTwoOfOrosPlayerIndex_whenSecondPlayerHasTwoOfOros_thenReturnIndexOne() {
        // Given
        final var twoOfOros = Card.builder().suit(Suit.OROS).number(2).build();
        final var playerOneId = "player-one";
        final var playerTwoId = "player-two";
        final var hands = new HashMap<String, List<Card>>();
        hands.put(playerOneId, List.of());
        hands.put(playerTwoId, List.of(twoOfOros));
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::playerOrder), List.of(playerOneId, playerTwoId))
                .set(field(GameSession::hands), hands)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();

        // When
        when(this.cardFactoryService.buildCard(Suit.OROS, 2)).thenReturn(twoOfOros);
        final var playerIndex = this.cardDealingService.findTwoOfOrosPlayerIndex(room);

        // Then
        verify(this.cardFactoryService, times(1)).buildCard(Suit.OROS, 2);
        assertEquals(1, playerIndex);
    }

    /** Test transfer highest cards when called then remove and add best cards. */
    @Test
    void testTransferHighestCards_whenCalled_thenRemoveAndAddBestCards() {
        // Given
        final var giverId = "giver-id";
        final var receiverId = "receiver-id";
        final var lowCard = Card.builder().suit(Suit.COPAS).number(3).build();
        final var highCard = Card.builder().suit(Suit.OROS).number(12).build();
        final var hands = Map.of(giverId, List.of(lowCard, highCard));
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::hands), hands)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();
        final var roomWithoutCards = Instancio.create(Room.class);
        final var roomAfterTransfer = Instancio.create(Room.class);
        final var bestCards = List.of(highCard);

        // When
        when(this.handManagementService.removeCardsFromHand(room, giverId, bestCards)).thenReturn(roomWithoutCards);
        when(this.handManagementService.addCardsToHand(roomWithoutCards, receiverId, bestCards))
                .thenReturn(roomAfterTransfer);
        final var result = this.cardDealingService.transferHighestCards(room, giverId, receiverId, 1);

        // Then
        verify(this.handManagementService, times(1)).removeCardsFromHand(room, giverId, bestCards);
        verify(this.handManagementService, times(1)).addCardsToHand(roomWithoutCards, receiverId, bestCards);
        assertEquals(roomAfterTransfer, result);
    }

    /** Test deal cards when not first game then increment play epoch. */
    @Test
    void testDealCards_whenNotFirstGame_thenIncrementPlayEpoch() {
        // Given
        final var playerOne = Instancio.of(Player.class)
                .set(field(Player::id), "player-one")
                .set(field(Player::role), PlayerRole.CULO)
                .create();
        final var playerTwo = Instancio.of(Player.class)
                .set(field(Player::id), "player-two")
                .set(field(Player::role), PlayerRole.GANADOR)
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::hostPlayerId), "player-one")
                .set(field(RoomLobby::players), List.of(playerOne, playerTwo))
                .create();
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::lastCuloId), "player-one")
                .set(field(GameSession::playEpoch), 1)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .set(field(Room::gameSession), gameSession)
                .create();
        final var cardOne = Card.builder().suit(Suit.COPAS).number(3).build();
        final var cardTwo = Card.builder().suit(Suit.ESPADAS).number(5).build();
        final var cardThree = Card.builder().suit(Suit.BASTOS).number(7).build();
        final var cardFour = Card.builder().suit(Suit.OROS).number(10).build();
        final var deck = List.of(cardOne, cardTwo, cardThree, cardFour);

        // When
        when(this.deckBuilderService.buildShuffledDeck()).thenReturn(deck);
        final var dealtRoom = this.cardDealingService.dealCards(room);

        // Then
        verify(this.deckBuilderService, times(1)).buildShuffledDeck();
        assertEquals(2, dealtRoom.gameSession().playEpoch());
        assertEquals(2, dealtRoom.gameSession().hands().get("player-one").size());
        assertEquals(2, dealtRoom.gameSession().hands().get("player-two").size());
        assertEquals(PlayerRole.NONE, dealtRoom.roomLobby().players().getFirst().role());
    }
}
