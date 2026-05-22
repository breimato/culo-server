package com.breixo.culo.domain.service.room;

import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Class PlayerLookupServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class PlayerLookupServiceImplTest {

    /** The player lookup service. */
    @InjectMocks
    PlayerLookupServiceImpl playerLookupService;

    /**
	 * Test find player by client id when player exists then return player.
	 */
    @Test
    void testFindPlayerByClientId_whenPlayerExists_thenReturnPlayer() {
        
        // Given
        final var player = Instancio.of(Player.class)
                .set(field(Player::clientId), "client-123")
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(player))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When
        final var playerOptional = this.playerLookupService.findPlayerByClientId(room, "client-123");

        // Then
        assertTrue(playerOptional.isPresent());
        assertEquals(player, playerOptional.get());
    }

    /**
	 * Test find player by id when player missing then return empty.
	 */
    @Test
    void testFindPlayerById_whenPlayerMissing_thenReturnEmpty() {
        
        // Given
        final var player = Instancio.of(Player.class)
                .set(field(Player::id), "player-id")
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(player))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When
        final var playerOptional = this.playerLookupService.findPlayerById(room, "other-id");

        // Then
        assertTrue(playerOptional.isEmpty());
    }
}
