package com.breixo.culo.application.usecase.game;

import com.breixo.culo.domain.GamePhase;
import com.breixo.culo.domain.PlayerRole;
import com.breixo.culo.domain.command.game.CuloSwapInitiateCommand;
import com.breixo.culo.domain.command.game.CuloSwapVoteCommand;
import com.breixo.culo.domain.model.Player;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuloSwapVoteUseCaseImplTest {

    @Mock
    RoomPersistencePort roomPersistencePort;

    CuloSwapInitiateUseCaseImpl initiateUseCase;
    CuloSwapVoteUseCaseImpl voteUseCase;

    @BeforeEach
    void setUp() {
        this.initiateUseCase = new CuloSwapInitiateUseCaseImpl(this.roomPersistencePort);
        this.voteUseCase = new CuloSwapVoteUseCaseImpl(this.roomPersistencePort);
    }

    @Test
    void testVote_whenTwoPlayersAndTargetAccepts_thenCompletesAndApproves() {
        final var room = new Room("ABCD", "culo-id");
        final var culo = Player.builder().id("culo-id").clientId("culo-client").nick("Culo").build();
        final var other = Player.builder().id("other-id").clientId("other-client").nick("Other").build();
        room.addPlayer(culo);
        room.addPlayer(other);
        culo.setRole(PlayerRole.CULO);
        other.setRole(PlayerRole.GANADOR);
        room.setPhase(GamePhase.DEALING);
        room.setLastCuloId("culo-id");
        room.getHands().put("culo-id", new java.util.ArrayList<>());
        room.getHands().put("other-id", new java.util.ArrayList<>());

        when(this.roomPersistencePort.findByCode("ABCD")).thenReturn(Optional.of(room));
        when(this.roomPersistencePort.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        this.initiateUseCase.execute(CuloSwapInitiateCommand.builder()
                .clientId("culo-client")
                .roomCode("ABCD")
                .targetPlayerId("other-id")
                .build());

        final var result = this.voteUseCase.execute(CuloSwapVoteCommand.builder()
                .clientId("other-client")
                .roomCode("ABCD")
                .accept(true)
                .build());

        assertTrue(result.completed());
        assertTrue(result.accepted());
        assertEquals(GamePhase.DEALING, result.room().getPhase());
        assertEquals("other-id", result.room().getLastCuloId());
    }
}
