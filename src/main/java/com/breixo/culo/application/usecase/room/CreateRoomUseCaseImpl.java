package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.command.room.CreateRoomCommand;
import com.breixo.culo.domain.model.Player;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.model.room.RoomJoinResult;
import com.breixo.culo.domain.port.input.room.CreateRoomUseCase;
import com.breixo.culo.domain.port.output.room.RoomCodeGenerationPort;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateRoomUseCaseImpl implements CreateRoomUseCase {

  /** The room persistence port. */
  private final RoomPersistencePort roomPersistencePort;

  /** The room code generation port. */
  private final RoomCodeGenerationPort roomCodeGenerationPort;

  @Override
  public RoomJoinResult execute(final CreateRoomCommand createRoomCommand) {
    final var playerId = UUID.randomUUID().toString();
    final var player = Player.builder()
        .id(playerId)
        .clientId(createRoomCommand.clientId())
        .nick(createRoomCommand.nick())
        .build();
    final var roomCode = this.roomCodeGenerationPort.generateUnique();
    final var room = new Room(roomCode, playerId);
    room.addPlayer(player);
    final var savedRoom = this.roomPersistencePort.save(room);
    return RoomJoinResult.builder()
        .roomCode(roomCode)
        .playerId(playerId)
        .room(savedRoom)
        .build();
  }
}
