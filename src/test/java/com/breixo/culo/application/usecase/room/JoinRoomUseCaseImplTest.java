package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.command.room.JoinRoomCommand;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomJoinResult;
import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import com.breixo.culo.domain.port.input.room.RoomMembershipService;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import com.breixo.culo.domain.port.output.room.RoomRetrievalPersistencePort;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class JoinRoomUseCaseImplTest.
 */
@ExtendWith(MockitoExtension.class)
class JoinRoomUseCaseImplTest {

    /** The join room use case impl. */
    @InjectMocks
    JoinRoomUseCaseImpl joinRoomUseCaseImpl;

    /** The room save persistence port. */
    @Mock
    RoomSavePersistencePort roomSavePersistencePort;

    /** The room retrieval persistence port. */
    @Mock
    RoomRetrievalPersistencePort roomRetrievalPersistencePort;

    /** The player lookup service. */
    @Mock
    PlayerLookupService playerLookupService;

    /** The room phase service. */
    @Mock
    RoomPhaseService roomPhaseService;

    /** The room membership service. */
    @Mock
    RoomMembershipService roomMembershipService;

    /**
	 * Test execute when room not found then throw room exception.
	 */
    @Test
    void testExecute_whenRoomNotFound_thenThrowRoomException() {
        
        // Given
        final var joinRoomCommand = Instancio.create(JoinRoomCommand.class);

        // When
        when(this.roomRetrievalPersistencePort.findByCode(joinRoomCommand.roomCode())).thenReturn(Optional.empty());
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.joinRoomUseCaseImpl.execute(joinRoomCommand));

        // Then
        assertEquals(RoomExceptionConstants.ROOM_NOT_FOUND, roomException.getMessage());
        verify(this.roomRetrievalPersistencePort, times(1)).findByCode(joinRoomCommand.roomCode());
    }

    /**
	 * Test execute when game already started then throw room exception.
	 */
    @Test
    void testExecute_whenGameAlreadyStarted_thenThrowRoomException() {
        
        // Given
        final var joinRoomCommand = Instancio.create(JoinRoomCommand.class);
        final var room = Instancio.create(Room.class);

        // When
        when(this.roomRetrievalPersistencePort.findByCode(joinRoomCommand.roomCode())).thenReturn(Optional.of(room));
        when(this.playerLookupService.findPlayerByClientId(room, joinRoomCommand.clientId())).thenReturn(Optional.empty());
        doThrow(new RoomException(RoomExceptionConstants.GAME_ALREADY_STARTED))
                .when(this.roomPhaseService).requireLobbyPhase(room);
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.joinRoomUseCaseImpl.execute(joinRoomCommand));

        // Then
        verify(this.roomRetrievalPersistencePort, times(1)).findByCode(joinRoomCommand.roomCode());
        verify(this.playerLookupService, times(1)).findPlayerByClientId(room, joinRoomCommand.clientId());
        verify(this.roomPhaseService, times(1)).requireLobbyPhase(room);
        assertEquals(RoomExceptionConstants.GAME_ALREADY_STARTED, roomException.getMessage());
    }

    /**
	 * Test execute when client reconnects then return existing player.
	 */
    @Test
    void testExecute_whenClientReconnects_thenReturnExistingPlayer() {
        
        // Given
        final var joinRoomCommand = Instancio.create(JoinRoomCommand.class);
        final var room = Instancio.create(Room.class);
        final var player = Instancio.create(Player.class);
        final var roomWithReconnect = Instancio.create(Room.class);
        final var savedRoom = Instancio.create(Room.class);
        final var roomJoinResult = RoomJoinResult.builder()
                .roomCode(savedRoom.roomLobby().code())
                .playerId(player.id())
                .room(savedRoom)
                .build();

        // When
        when(this.roomRetrievalPersistencePort.findByCode(joinRoomCommand.roomCode())).thenReturn(Optional.of(room));
        when(this.playerLookupService.findPlayerByClientId(room, joinRoomCommand.clientId())).thenReturn(Optional.of(player));
        when(this.roomMembershipService.reconnectPlayer(room, player)).thenReturn(roomWithReconnect);
        when(this.roomSavePersistencePort.save(roomWithReconnect)).thenReturn(savedRoom);
        final var result = this.joinRoomUseCaseImpl.execute(joinRoomCommand);

        // Then
        verify(this.roomRetrievalPersistencePort, times(1)).findByCode(joinRoomCommand.roomCode());
        verify(this.playerLookupService, times(1)).findPlayerByClientId(room, joinRoomCommand.clientId());
        verify(this.roomMembershipService, times(1)).reconnectPlayer(room, player);
        verify(this.roomSavePersistencePort, times(1)).save(roomWithReconnect);
        assertEquals(roomJoinResult.playerId(), result.playerId());
        assertEquals(roomJoinResult.roomCode(), result.roomCode());
        assertEquals(savedRoom, result.room());
    }
}
