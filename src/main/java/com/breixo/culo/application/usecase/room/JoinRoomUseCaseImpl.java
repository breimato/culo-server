package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.GamePhase;
import com.breixo.culo.domain.command.room.JoinRoomCommand;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.Player;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.model.room.RoomJoinResult;
import com.breixo.culo.domain.port.input.room.JoinRoomUseCase;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JoinRoomUseCaseImpl implements JoinRoomUseCase {

  /** The room persistence port. */
  private final RoomPersistencePort roomPersistencePort;

  @Override
  public RoomJoinResult execute(final JoinRoomCommand joinRoomCommand) {

    final var room = this.findRoomOrThrow(joinRoomCommand.roomCode());
    final var existingPlayer = room.findPlayerByClientId(joinRoomCommand.clientId());

    if (existingPlayer.isPresent()) {
      return this.reconnectPlayer(room, existingPlayer.get());
    }

    if (!room.getPhase().equals(GamePhase.LOBBY)) {
      throw new RoomException(RoomExceptionConstants.GAME_ALREADY_STARTED);
    }

    final var playerId = UUID.randomUUID().toString();
    final var player = Player.builder()
        .id(playerId)
        .clientId(joinRoomCommand.clientId())
        .nick(joinRoomCommand.nick())
        .build();
    room.addPlayer(player);

    final var savedRoom = this.roomPersistencePort.save(room);
    return RoomJoinResult.builder()
        .roomCode(savedRoom.getCode())
        .playerId(playerId)
        .room(savedRoom)
        .build();
  }

  private RoomJoinResult reconnectPlayer(final Room room, final Player player) {

    player.setConnected(true);

    final var savedRoom = this.roomPersistencePort.save(room);

    return RoomJoinResult.builder()
        .roomCode(savedRoom.getCode())
        .playerId(player.getId())
        .room(savedRoom)
        .build();
  }

  private Room findRoomOrThrow(final String roomCode) {
    return this.roomPersistencePort.findByCode(roomCode)
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.ROOM_NOT_FOUND));
  }
}
