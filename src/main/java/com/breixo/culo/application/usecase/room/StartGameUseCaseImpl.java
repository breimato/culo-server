package com.breixo.culo.application.usecase.room;

import com.breixo.culo.domain.GamePhase;
import com.breixo.culo.domain.command.room.StartGameCommand;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.port.input.room.StartGameUseCase;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartGameUseCaseImpl implements StartGameUseCase {

  private static final int MIN_PLAYERS = 2;

  /** The room persistence port. */
  private final RoomPersistencePort roomPersistencePort;

  @Override
  public Room execute(final StartGameCommand startGameCommand) {
    final var room = this.roomPersistencePort.findByCode(startGameCommand.roomCode())
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.ROOM_NOT_FOUND));
    final var player = room.findPlayerByClientId(startGameCommand.clientId())
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.PLAYER_NOT_IN_ROOM));
    if (!room.isHost(player.getId())) {
      throw new RoomException(RoomExceptionConstants.NOT_HOST);
    }
    if (!room.getPhase().equals(GamePhase.LOBBY)) {
      throw new RoomException(RoomExceptionConstants.GAME_ALREADY_STARTED);
    }
    if (room.getPlayers().size() < MIN_PLAYERS) {
      throw new RoomException(RoomExceptionConstants.NOT_ENOUGH_PLAYERS);
    }
    room.setPhase(GamePhase.DEALING);
    return this.roomPersistencePort.save(room);
  }
}
