package com.breixo.culo.application.usecase.game;

import com.breixo.culo.domain.command.game.PassCommand;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.game.PassResult;
import com.breixo.culo.domain.model.room.GameSessionContext;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.game.PassExecutionService;
import com.breixo.culo.domain.port.input.game.PlayPolicyService;
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

/** The Class Pass Use Case Impl Test. */
@ExtendWith(MockitoExtension.class)
class PassUseCaseImplTest {

    /** The pass use case. */
    @InjectMocks
    PassUseCaseImpl passUseCaseImpl;

    /** The room save persistence port. */
    @Mock
    RoomSavePersistencePort roomSavePersistencePort;

    /** The game context service. */
    @Mock
    GameContextService gameContextService;

    /** The play policy service. */
    @Mock
    PlayPolicyService playPolicyService;

    /** The pass execution service. */
    @Mock
    PassExecutionService passExecutionService;

    /** Test execute when command is valid then return pass result. */
    @Test
    void testExecute_whenCommandIsValid_thenReturnPassResult() {
        // Given
        final var passCommand = Instancio.create(PassCommand.class);
        final var gameSessionContext = Instancio.create(GameSessionContext.class);
        final var passResult = Instancio.create(PassResult.class);
        final var savedRoom = Instancio.create(Room.class);

        // When
        when(this.gameContextService.loadWithPhase(
                passCommand.roomCode(),
                passCommand.clientId(),
                GamePhase.PLAYING)).thenReturn(gameSessionContext);
        when(this.passExecutionService.execute(
                gameSessionContext.room(),
                gameSessionContext.player())).thenReturn(passResult);
        when(this.roomSavePersistencePort.save(passResult.room())).thenReturn(savedRoom);
        doNothing().when(this.playPolicyService).validatePlayerCanPlay(gameSessionContext);
        final var result = this.passUseCaseImpl.execute(passCommand);

        // Then
        verify(this.gameContextService, times(1)).loadWithPhase(
                passCommand.roomCode(),
                passCommand.clientId(),
                GamePhase.PLAYING);
        verify(this.playPolicyService, times(1)).validatePlayerCanPlay(gameSessionContext);
        verify(this.passExecutionService, times(1)).execute(
                gameSessionContext.room(),
                gameSessionContext.player());
        verify(this.roomSavePersistencePort, times(1)).save(passResult.room());
        assertEquals(savedRoom, result.room());
        assertEquals(passResult.playerId(), result.playerId());
        assertEquals(passResult.roundClosed(), result.roundClosed());
    }

    /** Test execute when not player turn then throw game exception. */
    @Test
    void testExecute_whenNotPlayerTurn_thenThrowGameException() {
        // Given
        final var passCommand = Instancio.create(PassCommand.class);
        final var gameSessionContext = Instancio.create(GameSessionContext.class);

        // When
        when(this.gameContextService.loadWithPhase(
                passCommand.roomCode(),
                passCommand.clientId(),
                GamePhase.PLAYING)).thenReturn(gameSessionContext);
        doThrow(new GameException(GameExceptionConstants.NOT_YOUR_TURN))
                .when(this.playPolicyService).validatePlayerCanPlay(gameSessionContext);
        final var gameException = assertThrows(
                GameException.class,
                () -> this.passUseCaseImpl.execute(passCommand));

        // Then
        verify(this.gameContextService, times(1)).loadWithPhase(
                passCommand.roomCode(),
                passCommand.clientId(),
                GamePhase.PLAYING);
        verify(this.playPolicyService, times(1)).validatePlayerCanPlay(gameSessionContext);
        assertEquals(GameExceptionConstants.NOT_YOUR_TURN, gameException.getMessage());
    }
}
