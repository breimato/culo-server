package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.command.room.StartGameCommand;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.GameSessionContext;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import com.breixo.culo.domain.port.input.room.StartGamePolicyService;
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

/**
 * The Class StartGameUseCaseImplTest.
 */
@ExtendWith(MockitoExtension.class)
class StartGameUseCaseImplTest {

    /** The start game use case impl. */
    @InjectMocks
    StartGameUseCaseImpl startGameUseCaseImpl;

    /** The room save persistence port. */
    @Mock
    RoomSavePersistencePort roomSavePersistencePort;

    /** The game context service. */
    @Mock
    GameContextService gameContextService;

    /** The room phase service. */
    @Mock
    RoomPhaseService roomPhaseService;

    /** The start game policy service. */
    @Mock
    StartGamePolicyService startGamePolicyService;

    /**
	 * Test execute when host and enough players then phase is dealing.
	 */
    @Test
    void testExecute_whenHostAndEnoughPlayers_thenPhaseIsDealing() {
        
        // Given
        final var startGameCommand = Instancio.create(StartGameCommand.class);
        final var gameSessionContext = Instancio.create(GameSessionContext.class);
        final var roomWithPhase = Instancio.create(Room.class);
        final var savedRoom = Instancio.create(Room.class);

        // When
        when(this.gameContextService.load(startGameCommand.roomCode(), startGameCommand.clientId()))
                .thenReturn(gameSessionContext);
        when(this.roomPhaseService.withPhase(gameSessionContext.room(), GamePhase.DEALING)).thenReturn(roomWithPhase);
        when(this.roomSavePersistencePort.save(roomWithPhase)).thenReturn(savedRoom);
        doNothing().when(this.roomPhaseService).requireLobbyPhase(gameSessionContext.room());
        doNothing().when(this.startGamePolicyService)
                .validateCanStart(gameSessionContext.room(), gameSessionContext.player());
        final var result = this.startGameUseCaseImpl.execute(startGameCommand);

        // Then
        verify(this.gameContextService, times(1)).load(startGameCommand.roomCode(), startGameCommand.clientId());
        verify(this.roomPhaseService, times(1)).requireLobbyPhase(gameSessionContext.room());
        verify(this.startGamePolicyService, times(1))
                .validateCanStart(gameSessionContext.room(), gameSessionContext.player());
        verify(this.roomPhaseService, times(1)).withPhase(gameSessionContext.room(), GamePhase.DEALING);
        verify(this.roomSavePersistencePort, times(1)).save(roomWithPhase);
        assertEquals(savedRoom, result);
    }

    /**
	 * Test execute when not host then throw room exception.
	 */
    @Test
    void testExecute_whenNotHost_thenThrowRoomException() {
        
        // Given
        final var startGameCommand = Instancio.create(StartGameCommand.class);
        final var gameSessionContext = Instancio.create(GameSessionContext.class);

        // When
        when(this.gameContextService.load(startGameCommand.roomCode(), startGameCommand.clientId()))
                .thenReturn(gameSessionContext);
        doNothing().when(this.roomPhaseService).requireLobbyPhase(gameSessionContext.room());
        doThrow(new RoomException(RoomExceptionConstants.NOT_HOST))
                .when(this.startGamePolicyService)
                .validateCanStart(gameSessionContext.room(), gameSessionContext.player());
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.startGameUseCaseImpl.execute(startGameCommand));

        // Then
        verify(this.gameContextService, times(1)).load(startGameCommand.roomCode(), startGameCommand.clientId());
        verify(this.roomPhaseService, times(1)).requireLobbyPhase(gameSessionContext.room());
        verify(this.startGamePolicyService, times(1))
                .validateCanStart(gameSessionContext.room(), gameSessionContext.player());
        assertEquals(RoomExceptionConstants.NOT_HOST, roomException.getMessage());
    }

    /**
	 * Test execute when not enough players then throw room exception.
	 */
    @Test
    void testExecute_whenNotEnoughPlayers_thenThrowRoomException() {
        
        // Given
        final var startGameCommand = Instancio.create(StartGameCommand.class);
        final var gameSessionContext = Instancio.create(GameSessionContext.class);

        // When
        when(this.gameContextService.load(startGameCommand.roomCode(), startGameCommand.clientId()))
                .thenReturn(gameSessionContext);
        doNothing().when(this.roomPhaseService).requireLobbyPhase(gameSessionContext.room());
        doThrow(new RoomException(RoomExceptionConstants.NOT_ENOUGH_PLAYERS))
                .when(this.startGamePolicyService)
                .validateCanStart(gameSessionContext.room(), gameSessionContext.player());
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.startGameUseCaseImpl.execute(startGameCommand));

        // Then
        verify(this.gameContextService, times(1)).load(startGameCommand.roomCode(), startGameCommand.clientId());
        verify(this.roomPhaseService, times(1)).requireLobbyPhase(gameSessionContext.room());
        verify(this.startGamePolicyService, times(1))
                .validateCanStart(gameSessionContext.room(), gameSessionContext.player());
        assertEquals(RoomExceptionConstants.NOT_ENOUGH_PLAYERS, roomException.getMessage());
    }
}
