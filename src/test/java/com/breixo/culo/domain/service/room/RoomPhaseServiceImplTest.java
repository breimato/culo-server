package com.breixo.culo.domain.service.room;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The Class RoomPhaseServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class RoomPhaseServiceImplTest {

    /** The room phase service. */
    @InjectMocks
    RoomPhaseServiceImpl roomPhaseService;

    /**
	 * Test require phase when phase matches then no exception.
	 */
    @Test
    void testRequirePhase_whenPhaseMatches_thenNoException() {
        
        // Given
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::phase), GamePhase.PLAYING)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When / Then
        this.roomPhaseService.requirePhase(room, GamePhase.PLAYING);
    }

    /**
	 * Test require phase when phase mismatches then throw game exception.
	 */
    @Test
    void testRequirePhase_whenPhaseMismatches_thenThrowGameException() {
        
        // Given
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::phase), GamePhase.LOBBY)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When
        final var gameException = assertThrows(
                GameException.class,
                () -> this.roomPhaseService.requirePhase(room, GamePhase.PLAYING));

        // Then
        assertEquals(GameExceptionConstants.WRONG_PHASE, gameException.getMessage());
    }

    /**
	 * Test require lobby phase when not lobby then throw room exception.
	 */
    @Test
    void testRequireLobbyPhase_whenNotLobby_thenThrowRoomException() {
        
        // Given
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::phase), GamePhase.PLAYING)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.roomPhaseService.requireLobbyPhase(room));

        // Then
        assertEquals(RoomExceptionConstants.GAME_ALREADY_STARTED, roomException.getMessage());
    }

    /**
	 * Test with phase when called then return room with new phase.
	 */
    @Test
    void testWithPhase_whenCalled_thenReturnRoomWithNewPhase() {
        
        // Given
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::phase), GamePhase.LOBBY)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When
        final var roomWithPhase = this.roomPhaseService.withPhase(room, GamePhase.DEALING);

        // Then
        assertEquals(GamePhase.DEALING, roomWithPhase.roomLobby().phase());
    }
}
