package com.breixo.culo.domain.service.room;

import com.breixo.culo.domain.constants.RoomConstants;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Class RoomMembershipServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class RoomMembershipServiceImplTest {

    /** The room membership service. */
    @InjectMocks
    RoomMembershipServiceImpl roomMembershipService;

    /**
	 * Test add player when room has space then add player and touch.
	 */
    @Test
    void testAddPlayer_whenRoomHasSpace_thenAddPlayerAndTouch() {
        
        // Given
        final var existingPlayer = Instancio.create(Player.class);
        final var newPlayer = Instancio.create(Player.class);
        final var lastActivity = Instant.parse("2020-01-01T00:00:00Z");
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(existingPlayer))
                .set(field(RoomLobby::lastActivity), lastActivity)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When
        final var roomWithNewPlayer = this.roomMembershipService.addPlayer(room, newPlayer);

        // Then
        assertEquals(2, roomWithNewPlayer.roomLobby().players().size());
        assertEquals(newPlayer, roomWithNewPlayer.roomLobby().players().get(1));
        assertTrue(roomWithNewPlayer.roomLobby().lastActivity().isAfter(lastActivity));
    }

    /**
	 * Test add player when room is full then throw room exception.
	 */
    @Test
    void testAddPlayer_whenRoomIsFull_thenThrowRoomException() {
        
        // Given
        final var players = new ArrayList<Player>();
        for (int playerIndex = 0; playerIndex < RoomConstants.MAX_PLAYERS; playerIndex++) {
            players.add(Instancio.create(Player.class));
        }
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.copyOf(players))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();
        final var newPlayer = Instancio.create(Player.class);

        // When
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.roomMembershipService.addPlayer(room, newPlayer));

        // Then
        assertEquals(RoomExceptionConstants.ROOM_FULL, roomException.getMessage());
    }

    /**
	 * Test reconnect player when player exists then mark connected and touch.
	 */
    @Test
    void testReconnectPlayer_whenPlayerExists_thenMarkConnectedAndTouch() {
        
        // Given
        final var disconnectedPlayer = Instancio.of(Player.class)
                .set(field(Player::id), "player-id")
                .set(field(Player::connected), false)
                .create();
        final var otherPlayer = Instancio.create(Player.class);
        final var lastActivity = Instant.parse("2020-01-01T00:00:00Z");
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(disconnectedPlayer, otherPlayer))
                .set(field(RoomLobby::lastActivity), lastActivity)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();
        final var reconnectedPlayer = disconnectedPlayer.toBuilder().connected(true).build();

        // When
        final var roomAfterReconnect = this.roomMembershipService.reconnectPlayer(room, reconnectedPlayer);

        // Then
        final var updatedPlayer = roomAfterReconnect.roomLobby().players().stream()
                .filter(player -> player.id().equals("player-id"))
                .findFirst()
                .orElseThrow();
        assertTrue(updatedPlayer.connected());
        assertTrue(roomAfterReconnect.roomLobby().lastActivity().isAfter(lastActivity));
    }

    /**
	 * Test touch when called then update last activity.
	 */
    @Test
    void testTouch_whenCalled_thenUpdateLastActivity() {
        
        // Given
        final var lastActivity = Instant.parse("2020-01-01T00:00:00Z");
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::lastActivity), lastActivity)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When
        final var touchedRoom = this.roomMembershipService.touch(room);

        // Then
        assertNotNull(touchedRoom.roomLobby().lastActivity());
        assertTrue(touchedRoom.roomLobby().lastActivity().isAfter(lastActivity));
    }
}
