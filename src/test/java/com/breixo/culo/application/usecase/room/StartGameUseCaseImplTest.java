package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.command.room.StartGameCommand;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.service.room.PlayerLookupServiceImpl;
import com.breixo.culo.domain.service.room.RoomPhaseServiceImpl;
import com.breixo.culo.domain.service.room.RoomStartPolicyValidationServiceImpl;
import com.breixo.culo.domain.service.session.GameSessionContextServiceImpl;
import com.breixo.culo.testsupport.InMemoryRoomStore;
import com.breixo.culo.testsupport.RoomTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** The Class Start Game Use Case Impl Test. */
@ExtendWith(MockitoExtension.class)
class StartGameUseCaseImplTest {

    final InMemoryRoomStore inMemoryRoomStore = new InMemoryRoomStore();

    final PlayerLookupServiceImpl playerLookupService = new PlayerLookupServiceImpl();

    final RoomPhaseServiceImpl roomPhaseService = new RoomPhaseServiceImpl();

    StartGameUseCaseImpl startGameUseCaseImpl;

    @BeforeEach
    void setUp() {
        final var gameSessionContextService = new GameSessionContextServiceImpl(
                this.inMemoryRoomStore,
                this.playerLookupService,
                this.roomPhaseService);
        this.startGameUseCaseImpl = new StartGameUseCaseImpl(
                this.inMemoryRoomStore,
                gameSessionContextService,
                this.roomPhaseService,
                new RoomStartPolicyValidationServiceImpl());
    }

    /** Test execute when host and enough players then phase is dealing. */
    @Test
    void testExecute_whenHostAndEnoughPlayers_thenPhaseIsDealing() {
        // Given
        final var startGameCommand = StartGameCommand.builder()
                .clientId("client-host")
                .roomCode("ABCD")
                .build();
        final var hostPlayer = RoomTestFactory.player("host-id", "client-host", "Host");
        final var guestPlayer = RoomTestFactory.player("guest-id", "client-guest", "Guest");
        final var room = RoomTestFactory.roomWithPlayers("ABCD", "host-id", List.of(hostPlayer, guestPlayer));
        this.inMemoryRoomStore.seed(room);

        // When
        final var savedRoom = this.startGameUseCaseImpl.execute(startGameCommand);

        // Then
        final var roomInStore = this.inMemoryRoomStore.findByCode(startGameCommand.roomCode()).orElseThrow();
        assertEquals(GamePhase.DEALING, savedRoom.roomLobby().phase());
        assertEquals(GamePhase.DEALING, roomInStore.roomLobby().phase());
    }

    /** Test execute when not host then throw room exception. */
    @Test
    void testExecute_whenNotHost_thenThrowRoomException() {
        // Given
        final var startGameCommand = StartGameCommand.builder()
                .clientId("client-guest")
                .roomCode("ABCD")
                .build();
        final var hostPlayer = RoomTestFactory.player("host-id", "client-host", "Host");
        final var guestPlayer = RoomTestFactory.player("guest-id", "client-guest", "Guest");
        final var room = RoomTestFactory.roomWithPlayers("ABCD", "host-id", List.of(hostPlayer, guestPlayer));
        this.inMemoryRoomStore.seed(room);

        // When
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.startGameUseCaseImpl.execute(startGameCommand));

        // Then
        assertEquals(RoomExceptionConstants.NOT_HOST, roomException.getMessage());
    }

    /** Test execute when not enough players then throw room exception. */
    @Test
    void testExecute_whenNotEnoughPlayers_thenThrowRoomException() {
        // Given
        final var startGameCommand = StartGameCommand.builder()
                .clientId("client-host")
                .roomCode("ABCD")
                .build();
        final var hostPlayer = RoomTestFactory.player("host-id", "client-host", "Host");
        final var room = RoomTestFactory.roomWithPlayers("ABCD", "host-id", List.of(hostPlayer));
        this.inMemoryRoomStore.seed(room);

        // When
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.startGameUseCaseImpl.execute(startGameCommand));

        // Then
        assertEquals(RoomExceptionConstants.NOT_ENOUGH_PLAYERS, roomException.getMessage());
    }
}
