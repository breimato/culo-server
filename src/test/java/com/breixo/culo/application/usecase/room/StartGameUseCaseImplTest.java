package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.GamePhase;
import com.breixo.culo.domain.command.room.StartGameCommand;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.Player;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The Class Start Game Use Case Impl Test. */
@ExtendWith(MockitoExtension.class)
class StartGameUseCaseImplTest {

  @Mock
  RoomPersistencePort roomPersistencePort;

  @InjectMocks
  StartGameUseCaseImpl startGameUseCaseImpl;

  /** Test execute when host and enough players then phase is dealing. */
  @Test
  void testExecute_whenHostAndEnoughPlayers_thenPhaseIsDealing() {
    // Given
    final var startGameCommand = StartGameCommand.builder()
        .clientId("client-host")
        .roomCode("ABCD")
        .build();
    final var hostPlayer = Player.builder()
        .id("host-id")
        .clientId("client-host")
        .nick("Host")
        .build();
    final var guestPlayer = Player.builder()
        .id("guest-id")
        .clientId("client-guest")
        .nick("Guest")
        .build();
    final var room = new Room(startGameCommand.roomCode(), "host-id");
    room.addPlayer(hostPlayer);
    room.addPlayer(guestPlayer);

    // When
    when(this.roomPersistencePort.findByCode(startGameCommand.roomCode())).thenReturn(Optional.of(room));
    when(this.roomPersistencePort.save(room)).thenReturn(room);
    final var savedRoom = this.startGameUseCaseImpl.execute(startGameCommand);

    // Then
    verify(this.roomPersistencePort, times(1)).save(room);
    assertEquals(GamePhase.DEALING, savedRoom.getPhase());
  }

  /** Test execute when not host then throw room exception. */
  @Test
  void testExecute_whenNotHost_thenThrowRoomException() {
    // Given
    final var startGameCommand = StartGameCommand.builder()
        .clientId("client-guest")
        .roomCode("ABCD")
        .build();
    final var hostPlayer = Player.builder()
        .id("host-id")
        .clientId("client-host")
        .nick("Host")
        .build();
    final var guestPlayer = Player.builder()
        .id("guest-id")
        .clientId("client-guest")
        .nick("Guest")
        .build();
    final var room = new Room(startGameCommand.roomCode(), "host-id");
    room.addPlayer(hostPlayer);
    room.addPlayer(guestPlayer);

    // When
    when(this.roomPersistencePort.findByCode(startGameCommand.roomCode())).thenReturn(Optional.of(room));

    // Then
    final var roomException = assertThrows(
        RoomException.class,
        () -> this.startGameUseCaseImpl.execute(startGameCommand));
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
    final var hostPlayer = Player.builder()
        .id("host-id")
        .clientId("client-host")
        .nick("Host")
        .build();
    final var room = new Room(startGameCommand.roomCode(), "host-id");
    room.addPlayer(hostPlayer);

    // When
    when(this.roomPersistencePort.findByCode(startGameCommand.roomCode())).thenReturn(Optional.of(room));

    // Then
    final var roomException = assertThrows(
        RoomException.class,
        () -> this.startGameUseCaseImpl.execute(startGameCommand));
    assertEquals(RoomExceptionConstants.NOT_ENOUGH_PLAYERS, roomException.getMessage());
  }
}
