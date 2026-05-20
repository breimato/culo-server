package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.command.room.CreateRoomCommand;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.port.output.room.RoomCodeGenerationPort;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The Class Create Room Use Case Impl Test. */
@ExtendWith(MockitoExtension.class)
class CreateRoomUseCaseImplTest {

  @Mock
  RoomSavePersistencePort roomSavePersistencePort;

  @Mock
  RoomCodeGenerationPort roomCodeGenerationPort;

  @InjectMocks
  CreateRoomUseCaseImpl createRoomUseCaseImpl;

  /** Test execute when command is valid then persist and return join result. */
  @Test
  void testExecute_whenCommandIsValid_thenPersistAndReturnJoinResult() {
    // Given
    final var createRoomCommand = Instancio.create(CreateRoomCommand.class);
    final var roomCode = "ABCD";
    final var room = Instancio.create(Room.class);

    // When
    when(this.roomCodeGenerationPort.execute()).thenReturn(roomCode);
    when(this.roomSavePersistencePort.save(any(Room.class))).thenReturn(room);
    final var roomJoinResult = this.createRoomUseCaseImpl.execute(createRoomCommand);

    // Then
    verify(this.roomCodeGenerationPort, times(1)).execute();
    verify(this.roomSavePersistencePort, times(1)).save(any(Room.class));
    assertEquals(roomCode, roomJoinResult.roomCode());
    assertNotNull(roomJoinResult.playerId());
    assertEquals(room, roomJoinResult.room());
    assertNotNull(roomJoinResult.room());
  }
}
