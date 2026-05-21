package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.command.room.JoinRoomCommand;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.service.room.PlayerLookupServiceImpl;
import com.breixo.culo.domain.service.room.RoomMembershipServiceImpl;
import com.breixo.culo.domain.service.room.RoomPhaseServiceImpl;
import com.breixo.culo.testsupport.InMemoryRoomStore;
import com.breixo.culo.testsupport.RoomTestFactory;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The Class Join Room Use Case Impl Test. */
@ExtendWith(MockitoExtension.class)
class JoinRoomUseCaseImplTest {

    final InMemoryRoomStore inMemoryRoomStore = new InMemoryRoomStore();

    final PlayerLookupServiceImpl playerLookupService = new PlayerLookupServiceImpl();

    final RoomPhaseServiceImpl roomPhaseService = new RoomPhaseServiceImpl();

    final RoomMembershipServiceImpl roomMembershipService = new RoomMembershipServiceImpl();

    JoinRoomUseCaseImpl joinRoomUseCaseImpl;

    @BeforeEach
    void setUp() {
        this.joinRoomUseCaseImpl = new JoinRoomUseCaseImpl(
                this.inMemoryRoomStore,
                this.inMemoryRoomStore,
                this.playerLookupService,
                this.roomPhaseService,
                this.roomMembershipService);
    }

    /** Test execute when room not found then throw room exception. */
    @Test
    void testExecute_whenRoomNotFound_thenThrowRoomException() {
        // Given
        final var joinRoomCommand = Instancio.create(JoinRoomCommand.class);

        // When
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.joinRoomUseCaseImpl.execute(joinRoomCommand));

        // Then
        assertEquals(RoomExceptionConstants.ROOM_NOT_FOUND, roomException.getMessage());
        assertTrue(this.inMemoryRoomStore.findByCode(joinRoomCommand.roomCode()).isEmpty());
    }

    /** Test execute when game already started then throw room exception. */
    @Test
    void testExecute_whenGameAlreadyStarted_thenThrowRoomException() {
        // Given
        final var joinRoomCommand = Instancio.create(JoinRoomCommand.class);
        final var room = RoomTestFactory.withPhase(
                RoomTestFactory.emptyRoom(joinRoomCommand.roomCode(), Instancio.create(String.class)),
                GamePhase.PLAYING);
        this.inMemoryRoomStore.seed(room);

        // When
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.joinRoomUseCaseImpl.execute(joinRoomCommand));

        // Then
        assertEquals(RoomExceptionConstants.GAME_ALREADY_STARTED, roomException.getMessage());
    }

    /** Test execute when client reconnects then return existing player. */
    @Test
    void testExecute_whenClientReconnects_thenReturnExistingPlayer() {
        // Given
        final var joinRoomCommand = JoinRoomCommand.builder()
                .clientId("client-1")
                .roomCode("WXYZ")
                .nick("Ana")
                .build();
        final var player = RoomTestFactory.player("player-1", "client-1", "Ana");
        final var room = RoomTestFactory.roomWithPlayers("WXYZ", "host-1", List.of(player));
        this.inMemoryRoomStore.seed(room);

        // When
        final var roomJoinResult = this.joinRoomUseCaseImpl.execute(joinRoomCommand);

        // Then
        final var savedRoom = this.inMemoryRoomStore.findByCode(joinRoomCommand.roomCode()).orElseThrow();
        assertEquals("player-1", roomJoinResult.playerId());
        assertEquals(joinRoomCommand.roomCode(), roomJoinResult.roomCode());
        assertTrue(savedRoom.roomLobby().players().stream()
                .filter(playerInRoom -> playerInRoom.id().equals("player-1"))
                .findFirst()
                .orElseThrow()
                .connected());
    }
}
