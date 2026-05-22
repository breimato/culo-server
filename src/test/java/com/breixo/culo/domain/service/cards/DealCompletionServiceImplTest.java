package com.breixo.culo.domain.service.cards;

import com.breixo.culo.domain.constants.GameConstants;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.cards.CardDealingService;
import com.breixo.culo.domain.port.input.player.PlayerRoleService;
import com.breixo.culo.domain.port.input.cards.QuadDiscardService;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The Class Deal Completion Service Impl Test. */
@ExtendWith(MockitoExtension.class)
class DealCompletionServiceImplTest {

    /** The deal completion service. */
    @InjectMocks
    DealCompletionServiceImpl dealCompletionService;

    /** The player role service. */
    @Mock
    PlayerRoleService playerRoleService;

    /** The card dealing service. */
    @Mock
    CardDealingService cardDealingService;

    /** The quad discard service. */
    @Mock
    QuadDiscardService quadDiscardService;

    /** The room phase service. */
    @Mock
    RoomPhaseService roomPhaseService;

    /** Test execute when exchange needed then phase is exchange. */
    @Test
    void testExecute_whenExchangeNeeded_thenPhaseIsExchange() {
        // Given
        final var room = Instancio.create(Room.class);
        final var dealtRoom = Instancio.create(Room.class);
        final var roomWithRoles = Instancio.create(Room.class);
        final var roomAfterTransfer = Instancio.create(Room.class);
        final var roomWithExchangePhase = Instancio.create(Room.class);
        final var rolesBeforeDeal = Map.of(
                PlayerRole.CULO, "culo-id",
                PlayerRole.GANADOR, "ganador-id");

        // When
        when(this.playerRoleService.captureExchangeRoles(room)).thenReturn(rolesBeforeDeal);
        when(this.playerRoleService.needsPostDealExchange(rolesBeforeDeal)).thenReturn(true);
        when(this.cardDealingService.dealCards(room)).thenReturn(dealtRoom);
        when(this.playerRoleService.updatePlayerRoles(dealtRoom, rolesBeforeDeal)).thenReturn(roomWithRoles);
        when(this.cardDealingService.transferHighestCards(
                roomWithRoles,
                "culo-id",
                "ganador-id",
                GameConstants.BEST_CARDS_FROM_CULO_TO_GANADOR)).thenReturn(roomAfterTransfer);
        when(this.roomPhaseService.withPhase(roomAfterTransfer, GamePhase.EXCHANGE))
                .thenReturn(roomWithExchangePhase);
        final var result = this.dealCompletionService.execute(room);

        // Then
        verify(this.playerRoleService, times(1)).captureExchangeRoles(room);
        verify(this.playerRoleService, times(1)).needsPostDealExchange(rolesBeforeDeal);
        verify(this.cardDealingService, times(1)).dealCards(room);
        verify(this.playerRoleService, times(1)).updatePlayerRoles(dealtRoom, rolesBeforeDeal);
        verify(this.cardDealingService, times(1)).transferHighestCards(
                roomWithRoles,
                "culo-id",
                "ganador-id",
                GameConstants.BEST_CARDS_FROM_CULO_TO_GANADOR);
        verify(this.roomPhaseService, times(1)).withPhase(roomAfterTransfer, GamePhase.EXCHANGE);
        assertEquals(roomWithExchangePhase, result);
    }

    /** Test execute when exchange not needed then phase is playing. */
    @Test
    void testExecute_whenExchangeNotNeeded_thenPhaseIsPlaying() {
        // Given
        final var room = Instancio.create(Room.class);
        final var dealtRoom = Instancio.create(Room.class);
        final var roomAfterQuads = Instancio.create(Room.class);
        final var roomWithPlayingPhase = Instancio.create(Room.class);
        final var rolesBeforeDeal = Map.<PlayerRole, String>of();

        // When
        when(this.playerRoleService.captureExchangeRoles(room)).thenReturn(rolesBeforeDeal);
        when(this.playerRoleService.needsPostDealExchange(rolesBeforeDeal)).thenReturn(false);
        when(this.cardDealingService.dealCards(room)).thenReturn(dealtRoom);
        when(this.quadDiscardService.discardQuadsForAllPlayers(dealtRoom)).thenReturn(roomAfterQuads);
        when(this.roomPhaseService.withPhase(roomAfterQuads, GamePhase.PLAYING)).thenReturn(roomWithPlayingPhase);
        final var result = this.dealCompletionService.execute(room);

        // Then
        verify(this.playerRoleService, times(1)).captureExchangeRoles(room);
        verify(this.playerRoleService, times(1)).needsPostDealExchange(rolesBeforeDeal);
        verify(this.cardDealingService, times(1)).dealCards(room);
        verify(this.quadDiscardService, times(1)).discardQuadsForAllPlayers(dealtRoom);
        verify(this.roomPhaseService, times(1)).withPhase(roomAfterQuads, GamePhase.PLAYING);
        assertEquals(roomWithPlayingPhase, result);
    }
}
