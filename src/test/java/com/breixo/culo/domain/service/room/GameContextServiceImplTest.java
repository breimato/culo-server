package com.breixo.culo.domain.service.room;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import com.breixo.culo.domain.port.output.room.RoomRetrievalPersistencePort;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class GameContextServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class GameContextServiceImplTest {

    /** The game context service. */
    @InjectMocks
    GameContextServiceImpl gameContextService;

    /** The room retrieval persistence port. */
    @Mock
    RoomRetrievalPersistencePort roomRetrievalPersistencePort;

    /** The player lookup service. */
    @Mock
    PlayerLookupService playerLookupService;

    /** The room phase service. */
    @Mock
    RoomPhaseService roomPhaseService;

    /**
	 * Test load when room exists then return game session context.
	 */
    @Test
    void testLoad_whenRoomExists_thenReturnGameSessionContext() {
        
        // Given
        final var roomCode = Instancio.create(String.class);
        final var clientId = Instancio.create(String.class);
        final var room = Instancio.create(Room.class);
        final var player = Instancio.create(Player.class);

        // When
        when(this.roomRetrievalPersistencePort.findByCode(roomCode)).thenReturn(Optional.of(room));
        when(this.playerLookupService.findPlayerByClientId(room, clientId)).thenReturn(Optional.of(player));
        final var gameSessionContext = this.gameContextService.load(roomCode, clientId);

        // Then
        verify(this.roomRetrievalPersistencePort, times(1)).findByCode(roomCode);
        verify(this.playerLookupService, times(1)).findPlayerByClientId(room, clientId);
        assertEquals(room, gameSessionContext.room());
        assertEquals(player, gameSessionContext.player());
    }

    /**
	 * Test load when room not found then throw room exception.
	 */
    @Test
    void testLoad_whenRoomNotFound_thenThrowRoomException() {
        
        // Given
        final var roomCode = Instancio.create(String.class);
        final var clientId = Instancio.create(String.class);

        // When
        when(this.roomRetrievalPersistencePort.findByCode(roomCode)).thenReturn(Optional.empty());
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.gameContextService.load(roomCode, clientId));

        // Then
        verify(this.roomRetrievalPersistencePort, times(1)).findByCode(roomCode);
        assertEquals(RoomExceptionConstants.ROOM_NOT_FOUND, roomException.getMessage());
    }

    /**
	 * Test require player turn when not current player then throw game exception.
	 */
    @Test
    void testRequirePlayerTurn_whenNotCurrentPlayer_thenThrowGameException() {
        
        // Given
        final var player = Instancio.of(Player.class)
                .set(field(Player::id), "player-b")
                .create();
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::playerOrder), List.of("player-a"))
                .set(field(GameSession::currentPlayerIndex), 0)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();

        // When
        final var gameException = assertThrows(
                GameException.class,
                () -> this.gameContextService.requirePlayerTurn(room, player));

        // Then
        assertEquals(GameExceptionConstants.NOT_YOUR_TURN, gameException.getMessage());
    }

    /**
	 * Test is player out when hand is empty then return true.
	 */
    @Test
    void testIsPlayerOut_whenHandIsEmpty_thenReturnTrue() {
        
        // Given
        final var room = Instancio.create(Room.class);
        final var playerId = Instancio.create(String.class);

        // When
        final var playerOut = this.gameContextService.isPlayerOut(room, playerId);

        // Then
        assertTrue(playerOut);
    }
}
