package com.breixo.culo.application.usecase.swap;

import com.breixo.culo.domain.command.swap.CuloSwapInitiateCommand;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.room.GameSessionContext;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.model.swap.CuloSwapVoteCast;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import com.breixo.culo.domain.port.input.swap.CuloService;
import com.breixo.culo.domain.port.input.swap.CuloSwapPolicyService;
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
 * The Class CuloSwapInitiateUseCaseImplTest.
 */
@ExtendWith(MockitoExtension.class)
class CuloSwapInitiateUseCaseImplTest {

    /** The culo swap initiate use case impl. */
    @InjectMocks
    CuloSwapInitiateUseCaseImpl culoSwapInitiateUseCaseImpl;

    /** The room save persistence port. */
    @Mock
    RoomSavePersistencePort roomSavePersistencePort;

    /** The game context service. */
    @Mock
    GameContextService gameContextService;

    /** The culo swap policy service. */
    @Mock
    CuloSwapPolicyService culoSwapPolicyService;

    /** The culo service. */
    @Mock
    CuloService culoService;

    /** The room phase service. */
    @Mock
    RoomPhaseService roomPhaseService;

    /**
	 * Test execute when command is valid then save and return room.
	 */
    @Test
    void testExecute_whenCommandIsValid_thenSaveAndReturnRoom() {
        
        // Given
        final var culoSwapInitiateCommand = Instancio.create(CuloSwapInitiateCommand.class);
        final var gameSessionContext = Instancio.create(GameSessionContext.class);
        final var roomAfterInitiate = Instancio.create(Room.class);
        final var roomWithPhase = Instancio.create(Room.class);
        final var voteCast = Instancio.create(CuloSwapVoteCast.class);
        final var savedRoom = Instancio.create(Room.class);

        // When
        when(this.gameContextService.loadWithPhase(
                culoSwapInitiateCommand.roomCode(),
                culoSwapInitiateCommand.clientId(),
                GamePhase.DEALING)).thenReturn(gameSessionContext);
        when(this.culoService.initiateSwap(
                gameSessionContext.room(),
                gameSessionContext.player().id(),
                culoSwapInitiateCommand.targetPlayerId())).thenReturn(roomAfterInitiate);
        when(this.roomPhaseService.withPhase(roomAfterInitiate, GamePhase.CULO_SWAP_VOTE))
                .thenReturn(roomWithPhase);
        when(this.culoService.registerVote(
                roomWithPhase,
                gameSessionContext.player().id(),
                true)).thenReturn(voteCast);
        when(this.roomSavePersistencePort.save(voteCast.room())).thenReturn(savedRoom);
        doNothing().when(this.culoSwapPolicyService).validateInitiator(gameSessionContext.player());
        doNothing().when(this.culoSwapPolicyService).validateNoActiveSwap(gameSessionContext.room());
        doNothing().when(this.culoSwapPolicyService)
                .validateTargetExists(gameSessionContext.room(), culoSwapInitiateCommand.targetPlayerId());
        final var room = this.culoSwapInitiateUseCaseImpl.execute(culoSwapInitiateCommand);

        // Then
        verify(this.gameContextService, times(1)).loadWithPhase(
                culoSwapInitiateCommand.roomCode(),
                culoSwapInitiateCommand.clientId(),
                GamePhase.DEALING);
        verify(this.culoSwapPolicyService, times(1)).validateInitiator(gameSessionContext.player());
        verify(this.culoSwapPolicyService, times(1)).validateNoActiveSwap(gameSessionContext.room());
        verify(this.culoSwapPolicyService, times(1))
                .validateTargetExists(gameSessionContext.room(), culoSwapInitiateCommand.targetPlayerId());
        verify(this.culoService, times(1)).initiateSwap(
                gameSessionContext.room(),
                gameSessionContext.player().id(),
                culoSwapInitiateCommand.targetPlayerId());
        verify(this.roomPhaseService, times(1)).withPhase(roomAfterInitiate, GamePhase.CULO_SWAP_VOTE);
        verify(this.culoService, times(1)).registerVote(
                roomWithPhase,
                gameSessionContext.player().id(),
                true);
        verify(this.roomSavePersistencePort, times(1)).save(voteCast.room());
        assertEquals(savedRoom, room);
    }

    /**
	 * Test execute when not culo then throw game exception.
	 */
    @Test
    void testExecute_whenNotCulo_thenThrowGameException() {
        
        // Given
        final var culoSwapInitiateCommand = Instancio.create(CuloSwapInitiateCommand.class);
        final var gameSessionContext = Instancio.create(GameSessionContext.class);

        // When
        when(this.gameContextService.loadWithPhase(
                culoSwapInitiateCommand.roomCode(),
                culoSwapInitiateCommand.clientId(),
                GamePhase.DEALING)).thenReturn(gameSessionContext);
        doThrow(new GameException(GameExceptionConstants.NOT_CULO))
                .when(this.culoSwapPolicyService).validateInitiator(gameSessionContext.player());
        final var gameException = assertThrows(
                GameException.class,
                () -> this.culoSwapInitiateUseCaseImpl.execute(culoSwapInitiateCommand));

        // Then
        verify(this.gameContextService, times(1)).loadWithPhase(
                culoSwapInitiateCommand.roomCode(),
                culoSwapInitiateCommand.clientId(),
                GamePhase.DEALING);
        verify(this.culoSwapPolicyService, times(1)).validateInitiator(gameSessionContext.player());
        assertEquals(GameExceptionConstants.NOT_CULO, gameException.getMessage());
    }
}
