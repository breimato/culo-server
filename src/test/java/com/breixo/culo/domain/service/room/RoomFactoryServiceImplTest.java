package com.breixo.culo.domain.service.room;

import com.breixo.culo.domain.command.room.CreateRoomCommand;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The Class Room Factory Service Impl Test. */
@ExtendWith(MockitoExtension.class)
class RoomFactoryServiceImplTest {

    /** The room factory service. */
    @InjectMocks
    RoomFactoryServiceImpl roomFactoryService;

    /** Test create empty room when command provided then build lobby session. */
    @Test
    void testCreateEmptyRoom_whenCommandProvided_thenBuildLobbySession() {
        // Given
        final var createRoomCommand = Instancio.create(CreateRoomCommand.class);
        final var roomCode = Instancio.create(String.class);
        final var hostPlayerId = Instancio.create(String.class);

        // When
        final var room = this.roomFactoryService.createEmptyRoom(createRoomCommand, roomCode, hostPlayerId);

        // Then
        assertEquals(roomCode, room.roomLobby().code());
        assertEquals(hostPlayerId, room.roomLobby().hostPlayerId());
        assertEquals(GamePhase.LOBBY, room.roomLobby().phase());
        assertTrue(room.roomLobby().players().isEmpty());
        assertNotNull(room.roomLobby().lastActivity());
        assertTrue(room.gameSession().hands().isEmpty());
        assertTrue(room.gameSession().playerOrder().isEmpty());
        assertTrue(room.gameSession().finishOrder().isEmpty());
        assertTrue(room.gameSession().pendingQuadDiscards().isEmpty());
        assertTrue(room.exchangeState().exchangeDone().isEmpty());
        assertTrue(room.culoSwapState().votes().isEmpty());
    }
}
