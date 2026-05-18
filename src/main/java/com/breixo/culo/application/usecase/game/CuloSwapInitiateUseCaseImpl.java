package com.breixo.culo.application.usecase.game;

import com.breixo.culo.domain.GamePhase;
import com.breixo.culo.domain.PlayerRole;
import com.breixo.culo.domain.command.game.CuloSwapInitiateCommand;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.port.input.game.CuloSwapInitiateUseCase;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CuloSwapInitiateUseCaseImpl implements CuloSwapInitiateUseCase {

  /** The room persistence port. */
  private final RoomPersistencePort roomPersistencePort;

  @Override
  public Room execute(final CuloSwapInitiateCommand command) {
    final var room = this.roomPersistencePort.findByCode(command.roomCode())
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.ROOM_NOT_FOUND));
    final var player = room.findPlayerByClientId(command.clientId())
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.PLAYER_NOT_IN_ROOM));
    if (!room.getPhase().equals(GamePhase.DEALING)) {
      throw new GameException(GameExceptionConstants.WRONG_PHASE);
    }
    if (player.getRole() != PlayerRole.CULO) {
      throw new GameException(GameExceptionConstants.NOT_CULO);
    }
    if (room.getCuloSwapInitiatorId() != null) {
      throw new GameException(GameExceptionConstants.SWAP_ALREADY_ACTIVE);
    }
    room.findPlayerById(command.targetPlayerId())
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.PLAYER_NOT_IN_ROOM));

    room.setCuloSwapInitiatorId(player.getId());
    room.setCuloSwapTargetId(command.targetPlayerId());
    room.registerCuloSwapVote(player.getId(), true);
    room.setPhase(GamePhase.CULO_SWAP_VOTE);
    return this.roomPersistencePort.save(room);
  }
}
