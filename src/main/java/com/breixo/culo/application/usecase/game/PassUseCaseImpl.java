package com.breixo.culo.application.usecase.game;

import com.breixo.culo.domain.GamePhase;
import com.breixo.culo.domain.RuleEngine;
import com.breixo.culo.domain.command.game.PassCommand;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.model.Round;
import com.breixo.culo.domain.model.game.PassResult;
import com.breixo.culo.domain.port.input.game.PassUseCase;
import com.breixo.culo.domain.port.output.room.RoomRetrievalPersistencePort;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The Class PassUseCaseImpl.
 */
@Component
@RequiredArgsConstructor
public class PassUseCaseImpl implements PassUseCase {

  /** The Constant RULE_ENGINE. */
  private static final RuleEngine RULE_ENGINE = new RuleEngine();

  private final RoomSavePersistencePort roomSavePersistencePort;

  private final RoomRetrievalPersistencePort roomRetrievalPersistencePort;

  /**
	 * Execute.
	 *
	 * @param passCommand the pass command
	 * @return the pass result
	 */
  @Override
  public PassResult execute(final PassCommand passCommand) {
 
    final var room = this.roomRetrievalPersistencePort.findByCode(passCommand.roomCode())
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.ROOM_NOT_FOUND));
    final var player = room.findPlayerByClientId(passCommand.clientId())
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.PLAYER_NOT_IN_ROOM));
    if (!room.getPhase().equals(GamePhase.PLAYING)) {
      throw new GameException(GameExceptionConstants.WRONG_PHASE);
    }
    if (!player.getId().equals(room.getCurrentPlayerId())) {
      throw new GameException(GameExceptionConstants.NOT_YOUR_TURN);
    }
    if (room.isPlayerOut(player.getId())) {
      throw new GameException(GameExceptionConstants.PLAYER_OUT);
    }

    room.discardQuads(player.getId());

    final var round = room.getCurrentRound();
    round.registerPass(player.getId());

    final var activePlayerIds = this.activePlayerIds(room);
    var roundEnded = this.closeRoundIfOthersAllPassed(room, round, activePlayerIds);
    if (!roundEnded) {
      room.advanceTurn(false);
      roundEnded = this.closeRoundIfOthersAllPassed(room, round, activePlayerIds);
    }

    final var savedRoom = this.roomSavePersistencePort.save(room);
    return PassResult.builder()
        .room(savedRoom)
        .playerId(player.getId())
        .roundEnded(roundEnded)
        .build();
  }

  /**
	 * Active player ids.
	 *
	 * @param room the room
	 * @return the list
	 */
  private List<String> activePlayerIds(final Room room) {
    return room.getPlayerOrder().stream()
        .filter(id -> !room.isPlayerOut(id))
        .toList();
  }

  /**
	 * Close round if others all passed.
	 *
	 * @param room            the room
	 * @param round           the round
	 * @param activePlayerIds the active player ids
	 * @return true, if successful
	 */
  private boolean closeRoundIfOthersAllPassed(
      final Room room,
      final Round round,
      final List<String> activePlayerIds) {
    if (!RULE_ENGINE.isRoundOver(round, activePlayerIds)) {
      return false;
    }
    room.finishRoundAndSetOpener();
    return true;
  }
}
