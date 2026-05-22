package com.breixo.culo.domain.service.swap;

import com.breixo.culo.domain.command.cards.CardInput;
import com.breixo.culo.domain.command.swap.ExchangeGiveCommand;
import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.Suit;
import com.breixo.culo.domain.model.room.ExchangeState;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.game.PlayBuilderService;
import com.breixo.culo.domain.port.input.player.HandManagementService;
import com.breixo.culo.domain.port.input.player.PlayerRoleService;
import com.breixo.culo.domain.port.input.cards.QuadDiscardService;
import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class ExchangeServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class ExchangeServiceImplTest {

    /** The exchange service. */
    @InjectMocks
    ExchangeServiceImpl exchangeService;

    /** The play builder service. */
    @Mock
    PlayBuilderService playBuilderService;

    /** The hand management service. */
    @Mock
    HandManagementService handManagementService;

    /** The player role service. */
    @Mock
    PlayerRoleService playerRoleService;

    /** The quad discard service. */
    @Mock
    QuadDiscardService quadDiscardService;

    /** The room phase service. */
    @Mock
    RoomPhaseService roomPhaseService;

    /** The player lookup service. */
    @Mock
    PlayerLookupService playerLookupService;

    /**
	 * Test process give when ganador gives cards then transfer to culo.
	 */
    @Test
    void testProcessGive_whenGanadorGivesCards_thenTransferToCulo() {
        
        // Given
        final var ganadorId = "ganador-id";
        final var culoId = "culo-id";
        final var cardInput = CardInput.builder().suit(Suit.COPAS).number(3).build();
        final var exchangeGiveCommand = ExchangeGiveCommand.builder()
                .clientId("client-id")
                .roomCode("ABCD")
                .cards(List.of(cardInput))
                .build();
        final var card = Card.builder().suit(Suit.COPAS).number(3).build();
        final var ganador = Instancio.of(Player.class)
                .set(field(Player::id), ganadorId)
                .set(field(Player::role), PlayerRole.GANADOR)
                .create();
        final var room = Instancio.create(Room.class);
        final var roomWithoutCards = Instancio.create(Room.class);
        final var roomAfterGive = Instancio.create(Room.class);

        // When
        when(this.playBuilderService.toCards(exchangeGiveCommand.cards(), room, ganadorId)).thenReturn(List.of(card));
        when(this.playerLookupService.findPlayerById(room, ganadorId)).thenReturn(Optional.of(ganador));
        when(this.playerRoleService.getPlayerIdByRole(room, PlayerRole.CULO)).thenReturn(Optional.of(culoId));
        when(this.handManagementService.removeCardsFromHand(room, ganadorId, List.of(card))).thenReturn(roomWithoutCards);
        when(this.handManagementService.addCardsToHand(roomWithoutCards, culoId, List.of(card))).thenReturn(roomAfterGive);
        final var result = this.exchangeService.processGive(room, ganadorId, exchangeGiveCommand);

        // Then
        verify(this.playBuilderService, times(1)).toCards(exchangeGiveCommand.cards(), room, ganadorId);
        verify(this.playerLookupService, times(1)).findPlayerById(room, ganadorId);
        verify(this.playerRoleService, times(1)).getPlayerIdByRole(room, PlayerRole.CULO);
        verify(this.handManagementService, times(1)).removeCardsFromHand(room, ganadorId, List.of(card));
        verify(this.handManagementService, times(1)).addCardsToHand(roomWithoutCards, culoId, List.of(card));
        assertEquals(roomAfterGive, result);
    }

    /**
	 * Test process give when player has no exchange role then return same room.
	 */
    @Test
    void testProcessGive_whenPlayerHasNoExchangeRole_thenReturnSameRoom() {
        
        // Given
        final var playerId = "player-id";
        final var exchangeGiveCommand = Instancio.create(ExchangeGiveCommand.class);
        final var card = Instancio.create(Card.class);
        final var player = Instancio.of(Player.class)
                .set(field(Player::id), playerId)
                .set(field(Player::role), PlayerRole.PENULTIMO)
                .create();
        final var room = Instancio.create(Room.class);

        // When
        when(this.playBuilderService.toCards(exchangeGiveCommand.cards(), room, playerId)).thenReturn(List.of(card));
        when(this.playerLookupService.findPlayerById(room, playerId)).thenReturn(Optional.of(player));
        final var result = this.exchangeService.processGive(room, playerId, exchangeGiveCommand);

        // Then
        verify(this.playBuilderService, times(1)).toCards(exchangeGiveCommand.cards(), room, playerId);
        verify(this.playerLookupService, times(1)).findPlayerById(room, playerId);
        assertEquals(room, result);
    }

    /**
	 * Test finalize if complete when exchange incomplete then return same room.
	 */
    @Test
    void testFinalizeIfComplete_whenExchangeIncomplete_thenReturnSameRoom() {
        
        // Given
        final var exchangeState = Instancio.of(ExchangeState.class)
                .set(field(ExchangeState::exchangeDone), Set.of())
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::exchangeState), exchangeState)
                .create();

        // When
        when(this.playerRoleService.getPlayerIdByRole(room, PlayerRole.GANADOR)).thenReturn(Optional.of("ganador-id"));
        when(this.playerRoleService.getPlayerIdByRole(room, PlayerRole.SUBCAMPEON)).thenReturn(Optional.of("sub-id"));
        final var result = this.exchangeService.finalizeIfComplete(room);

        // Then
        verify(this.playerRoleService, times(1)).getPlayerIdByRole(room, PlayerRole.GANADOR);
        verify(this.playerRoleService, times(1)).getPlayerIdByRole(room, PlayerRole.SUBCAMPEON);
        assertEquals(room, result);
    }

    /**
	 * Test finalize if complete when exchange complete then phase is playing.
	 */
    @Test
    void testFinalizeIfComplete_whenExchangeComplete_thenPhaseIsPlaying() {
        
        // Given
        final var ganadorId = "ganador-id";
        final var subcampeonId = "sub-id";
        final var exchangeState = Instancio.of(ExchangeState.class)
                .set(field(ExchangeState::exchangeDone), Set.of(ganadorId, subcampeonId))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::exchangeState), exchangeState)
                .create();
        final var roomAfterReset = Instancio.create(Room.class);
        final var roomAfterQuads = Instancio.create(Room.class);
        final var roomWithPlayingPhase = Instancio.create(Room.class);

        // When
        when(this.playerRoleService.getPlayerIdByRole(room, PlayerRole.GANADOR)).thenReturn(Optional.of(ganadorId));
        when(this.playerRoleService.getPlayerIdByRole(room, PlayerRole.SUBCAMPEON)).thenReturn(Optional.of(subcampeonId));
        when(this.playerRoleService.resetPlayerRoles(room)).thenReturn(roomAfterReset);
        when(this.quadDiscardService.discardQuadsForAllPlayers(roomAfterReset)).thenReturn(roomAfterQuads);
        when(this.roomPhaseService.withPhase(roomAfterQuads, GamePhase.PLAYING)).thenReturn(roomWithPlayingPhase);
        final var result = this.exchangeService.finalizeIfComplete(room);

        // Then
        verify(this.playerRoleService, times(1)).getPlayerIdByRole(room, PlayerRole.GANADOR);
        verify(this.playerRoleService, times(1)).getPlayerIdByRole(room, PlayerRole.SUBCAMPEON);
        verify(this.playerRoleService, times(1)).resetPlayerRoles(room);
        verify(this.quadDiscardService, times(1)).discardQuadsForAllPlayers(roomAfterReset);
        verify(this.roomPhaseService, times(1)).withPhase(roomAfterQuads, GamePhase.PLAYING);
        assertEquals(roomWithPlayingPhase, result);
    }
}
