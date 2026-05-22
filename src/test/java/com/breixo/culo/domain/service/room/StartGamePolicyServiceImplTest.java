package com.breixo.culo.domain.service.room;

import com.breixo.culo.domain.command.room.StartGameCommand;
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

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The Class StartGamePolicyServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class StartGamePolicyServiceImplTest {

    /** The start game policy service. */
    @InjectMocks
    StartGamePolicyServiceImpl startGamePolicyService;

    /**
	 * Test validate can start when host and enough players then no exception.
	 */
    @Test
    void testValidateCanStart_whenHostAndEnoughPlayers_thenNoException() {
        
        // Given
        final var startGameCommand = Instancio.create(StartGameCommand.class);
        final var player = Instancio.of(Player.class)
                .set(field(Player::id), "host-id")
                .set(field(Player::clientId), startGameCommand.clientId())
                .create();
        final var guestPlayer = Instancio.create(Player.class);
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), Instancio.of(RoomLobby.class)
                        .set(field(RoomLobby::hostPlayerId), "host-id")
                        .set(field(RoomLobby::players), List.of(player, guestPlayer))
                        .create())
                .create();

        // When / Then
        assertDoesNotThrow(() -> this.startGamePolicyService.validateCanStart(room, player));
    }

    /**
	 * Test validate can start when not host then throw room exception.
	 */
    @Test
    void testValidateCanStart_whenNotHost_thenThrowRoomException() {
        
        // Given
        final var startGameCommand = Instancio.create(StartGameCommand.class);
        final var hostPlayer = Instancio.of(Player.class)
                .set(field(Player::id), "host-id")
                .create();
        final var guestPlayer = Instancio.of(Player.class)
                .set(field(Player::clientId), startGameCommand.clientId())
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), Instancio.of(RoomLobby.class)
                        .set(field(RoomLobby::hostPlayerId), "host-id")
                        .set(field(RoomLobby::players), List.of(hostPlayer, guestPlayer))
                        .create())
                .create();

        // When
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.startGamePolicyService.validateCanStart(room, guestPlayer));

        // Then
        assertEquals(RoomExceptionConstants.NOT_HOST, roomException.getMessage());
    }
}
