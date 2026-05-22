package com.breixo.culo.application.usecase.cards;

import com.breixo.culo.domain.command.cards.DealCardsCommand;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.GameSessionContext;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.cards.DealCompletionService;
import com.breixo.culo.domain.port.input.cards.DealPolicyService;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The Class Deal Cards Use Case Impl Test. */
@ExtendWith(MockitoExtension.class)
class DealCardsUseCaseImplTest {

    /** The deal cards use case. */
    @InjectMocks
    DealCardsUseCaseImpl dealCardsUseCaseImpl;

    /** The room save persistence port. */
    @Mock
    RoomSavePersistencePort roomSavePersistencePort;

    /** The game context service. */
    @Mock
    GameContextService gameContextService;

    /** The deal policy service. */
    @Mock
    DealPolicyService dealPolicyService;

    /** The deal completion service. */
    @Mock
    DealCompletionService dealCompletionService;

    /** Test execute when host and first game then deals cards. */
    @Test
    void testExecute_whenHostAndFirstGame_thenDealsCards() {
        // Given
        final var dealCardsCommand = Instancio.create(DealCardsCommand.class);
        final var gameSessionContext = Instancio.create(GameSessionContext.class);
        final var roomAfterDealing = Instancio.create(Room.class);
        final var savedRoom = Instancio.create(Room.class);

        // When
        when(this.gameContextService.loadWithPhase(
                dealCardsCommand.roomCode(),
                dealCardsCommand.clientId(),
                GamePhase.DEALING)).thenReturn(gameSessionContext);
        when(this.dealCompletionService.execute(gameSessionContext.room())).thenReturn(roomAfterDealing);
        when(this.roomSavePersistencePort.save(roomAfterDealing)).thenReturn(savedRoom);
        doNothing().when(this.dealPolicyService)
                .validateDealingAuthority(gameSessionContext.room(), gameSessionContext.player());
        final var result = this.dealCardsUseCaseImpl.execute(dealCardsCommand);

        // Then
        verify(this.gameContextService, times(1)).loadWithPhase(
                dealCardsCommand.roomCode(),
                dealCardsCommand.clientId(),
                GamePhase.DEALING);
        verify(this.dealPolicyService, times(1))
                .validateDealingAuthority(gameSessionContext.room(), gameSessionContext.player());
        verify(this.dealCompletionService, times(1)).execute(gameSessionContext.room());
        verify(this.roomSavePersistencePort, times(1)).save(roomAfterDealing);
        assertEquals(savedRoom, result);
    }

    /** Test execute when not host on first game then throw room exception. */
    @Test
    void testExecute_whenNotHostOnFirstGame_thenThrowRoomException() {
        // Given
        final var dealCardsCommand = Instancio.create(DealCardsCommand.class);
        final var gameSessionContext = Instancio.create(GameSessionContext.class);

        // When
        when(this.gameContextService.loadWithPhase(
                dealCardsCommand.roomCode(),
                dealCardsCommand.clientId(),
                GamePhase.DEALING)).thenReturn(gameSessionContext);
        doThrow(new RoomException(RoomExceptionConstants.NOT_HOST))
                .when(this.dealPolicyService)
                .validateDealingAuthority(gameSessionContext.room(), gameSessionContext.player());
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.dealCardsUseCaseImpl.execute(dealCardsCommand));

        // Then
        verify(this.gameContextService, times(1)).loadWithPhase(
                dealCardsCommand.roomCode(),
                dealCardsCommand.clientId(),
                GamePhase.DEALING);
        verify(this.dealPolicyService, times(1))
                .validateDealingAuthority(gameSessionContext.room(), gameSessionContext.player());
        assertEquals(RoomExceptionConstants.NOT_HOST, roomException.getMessage());
    }
}
