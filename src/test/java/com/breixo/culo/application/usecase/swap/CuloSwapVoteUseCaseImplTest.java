package com.breixo.culo.application.usecase.swap;

import com.breixo.culo.domain.command.swap.CuloSwapVoteCommand;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The Class Culo Swap Vote Use Case Impl Test. */
@ExtendWith(MockitoExtension.class)
class CuloSwapVoteUseCaseImplTest {

    @InjectMocks
    CuloSwapVoteUseCaseImpl culoSwapVoteUseCaseImpl;

    @Mock
    RoomSavePersistencePort roomSavePersistencePort;

    @Mock
    GameContextService gameContextService;

    @Mock
    CuloSwapPolicyService culoSwapPolicyService;

    @Mock
    CuloService culoService;

    @Mock
    RoomPhaseService roomPhaseService;

    @Test
    void testExecute_whenNotAllVoted_thenSaveRoomWithoutClosingPoll() {
        final var culoSwapVoteCommand = Instancio.create(CuloSwapVoteCommand.class);
        final var gameSessionContext = Instancio.create(GameSessionContext.class);
        final var roomAfterVote = Instancio.create(Room.class);
        final var culoSwapVoteCast = CuloSwapVoteCast.builder()
                .room(roomAfterVote)
                .allPlayersHaveVoted(false)
                .build();
        final var savedRoom = Instancio.create(Room.class);

        when(this.gameContextService.loadWithPhase(
                culoSwapVoteCommand.roomCode(),
                culoSwapVoteCommand.clientId(),
                GamePhase.CULO_SWAP_VOTE)).thenReturn(gameSessionContext);
        when(this.culoService.registerVote(
                gameSessionContext.room(),
                gameSessionContext.player().id(),
                culoSwapVoteCommand.accept())).thenReturn(culoSwapVoteCast);
        when(this.roomSavePersistencePort.save(roomAfterVote)).thenReturn(savedRoom);
        doNothing().when(this.culoSwapPolicyService)
                .validateNotAlreadyVoted(gameSessionContext.room(), gameSessionContext.player());
        final var culoSwapVoteResponse = this.culoSwapVoteUseCaseImpl.execute(culoSwapVoteCommand);

        verify(this.gameContextService, times(1)).loadWithPhase(
                culoSwapVoteCommand.roomCode(),
                culoSwapVoteCommand.clientId(),
                GamePhase.CULO_SWAP_VOTE);
        verify(this.culoSwapPolicyService, times(1))
                .validateNotAlreadyVoted(gameSessionContext.room(), gameSessionContext.player());
        verify(this.culoService, times(1)).registerVote(
                gameSessionContext.room(),
                gameSessionContext.player().id(),
                culoSwapVoteCommand.accept());
        verify(this.roomSavePersistencePort, times(1)).save(roomAfterVote);
        assertEquals(savedRoom, culoSwapVoteResponse.room());
        assertFalse(culoSwapVoteResponse.votingFinished());
    }
}