package com.breixo.culo.application.usecase.game;

import com.breixo.culo.domain.GamePhase;
import com.breixo.culo.domain.command.game.CuloSwapVoteCommand;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.model.game.CuloSwapVoteResult;
import com.breixo.culo.domain.port.input.game.CuloSwapVoteUseCase;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CuloSwapVoteUseCaseImpl implements CuloSwapVoteUseCase {

  /** The room persistence port. */
  private final RoomPersistencePort roomPersistencePort;

  @Override
  public CuloSwapVoteResult execute(final CuloSwapVoteCommand command) {
    final var room = this.roomPersistencePort.findByCode(command.roomCode())
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.ROOM_NOT_FOUND));
    final var player = room.findPlayerByClientId(command.clientId())
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.PLAYER_NOT_IN_ROOM));
    if (!room.getPhase().equals(GamePhase.CULO_SWAP_VOTE)) {
      throw new GameException(GameExceptionConstants.WRONG_PHASE);
    }
    if (room.getCuloSwapVotes().containsKey(player.getId())) {
      throw new GameException(GameExceptionConstants.SWAP_ALREADY_VOTED);
    }

    final var allVoted = room.registerCuloSwapVote(player.getId(), command.accept());
    var completed = false;
    var accepted = false;
    if (allVoted) {
      accepted = room.isCuloSwapApproved();
      if (accepted) {
        room.applyCuloSwap();
      }
      room.clearCuloSwap();
      room.setPhase(GamePhase.DEALING);
      completed = true;
    }

    final var savedRoom = this.roomPersistencePort.save(room);
    return CuloSwapVoteResult.builder()
        .room(savedRoom)
        .completed(completed)
        .accepted(accepted)
        .build();
  }
}
