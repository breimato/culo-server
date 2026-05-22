package com.breixo.culo.domain.service.cards;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The Class DealPolicyServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class DealPolicyServiceImplTest {

    /** The deal policy service. */
    @InjectMocks
    DealPolicyServiceImpl dealPolicyService;

    /**
	 * Test validate dealing authority when player is culo then no exception.
	 */
    @Test
    void testValidateDealingAuthority_whenPlayerIsCulo_thenNoException() {
        
        // Given
        final var player = Instancio.of(Player.class)
                .set(field(Player::role), PlayerRole.CULO)
                .create();
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::lastCuloId), "previous-culo")
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();

        // When / Then
        assertDoesNotThrow(() -> this.dealPolicyService.validateDealingAuthority(room, player));
    }

    /**
	 * Test validate dealing authority when not culo and not first game then throw
	 * game exception.
	 */
    @Test
    void testValidateDealingAuthority_whenNotCuloAndNotFirstGame_thenThrowGameException() {
        
        // Given
        final var player = Instancio.of(Player.class)
                .set(field(Player::role), PlayerRole.GANADOR)
                .create();
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::lastCuloId), "previous-culo")
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();

        // When
        final var gameException = assertThrows(
                GameException.class,
                () -> this.dealPolicyService.validateDealingAuthority(room, player));

        // Then
        assertEquals(GameExceptionConstants.NOT_CULO, gameException.getMessage());
    }

    /**
	 * Test validate dealing authority when first game and not host then throw room
	 * exception.
	 */
    @Test
    void testValidateDealingAuthority_whenFirstGameAndNotHost_thenThrowRoomException() {
        
        // Given
        final var player = Instancio.of(Player.class)
                .set(field(Player::id), "player-id")
                .set(field(Player::role), PlayerRole.GANADOR)
                .create();
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::lastCuloId), null)
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::hostPlayerId), "host-id")
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.dealPolicyService.validateDealingAuthority(room, player));

        // Then
        assertEquals(RoomExceptionConstants.NOT_HOST, roomException.getMessage());
    }
}
