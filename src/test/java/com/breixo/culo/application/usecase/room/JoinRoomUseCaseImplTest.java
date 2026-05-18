package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.GamePhase;
import com.breixo.culo.domain.command.room.JoinRoomCommand;
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

/** The Class Join Room Use Case Impl Test. */
@ExtendWith(MockitoExtension.class)
class JoinRoomUseCaseImplTest {

  @Mock
  RoomPersistencePort roomPersistencePort;

  @InjectMocks
  JoinRoomUseCaseImpl joinRoomUseCaseImpl;

  /** Test execute when room not found then throw room exception. */
  @Test
  void testExecute_whenRoomNotFound_thenThrowRoomException() {
    // Given
    final var joinRoomCommand = Instancio.create(JoinRoomCommand.class);

    // When
    when(this.roomPersistencePort.findByCode(joinRoomCommand.roomCode())).thenReturn(Optional.empty());

    // Then
    final var roomException = assertThrows(
        RoomException.class,
        () -> this.joinRoomUseCaseImpl.execute(joinRoomCommand));
    verify(this.roomPersistencePort, times(1)).findByCode(joinRoomCommand.roomCode());
    assertEquals(RoomExceptionConstants.ROOM_NOT_FOUND, roomException.getMessage());
  }

  /** Test execute when game already started then throw room exception. */
  @Test
  void testExecute_whenGameAlreadyStarted_thenThrowRoomException() {
    // Given
    final var joinRoomCommand = Instancio.create(JoinRoomCommand.class);
    final var room = new Room(joinRoomCommand.roomCode(), Instancio.create(String.class));
    room.setPhase(GamePhase.PLAYING);

    // When
    when(this.roomPersistencePort.findByCode(joinRoomCommand.roomCode())).thenReturn(Optional.of(room));

    // Then
    final var roomException = assertThrows(
        RoomException.class,
        () -> this.joinRoomUseCaseImpl.execute(joinRoomCommand));
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
    final var player = Player.builder()
        .id("player-1")
        .clientId("client-1")
        .nick("Ana")
        .build();
    final var room = new Room(joinRoomCommand.roomCode(), "host-1");
    room.addPlayer(player);

    // When
    when(this.roomPersistencePort.findByCode(joinRoomCommand.roomCode())).thenReturn(Optional.of(room));
    when(this.roomPersistencePort.save(room)).thenReturn(room);
    final var roomJoinResult = this.joinRoomUseCaseImpl.execute(joinRoomCommand);

    // Then
    verify(this.roomPersistencePort, times(1)).save(room);
    assertEquals("player-1", roomJoinResult.playerId());
    assertEquals(joinRoomCommand.roomCode(), roomJoinResult.roomCode());
  }
}
