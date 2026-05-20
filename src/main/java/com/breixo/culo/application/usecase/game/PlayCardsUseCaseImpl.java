package com.breixo.culo.application.usecase.game;

import com.breixo.culo.domain.GamePhase;
import com.breixo.culo.domain.RuleEngine;
import com.breixo.culo.domain.command.game.PlayCardsCommand;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.Card;
import com.breixo.culo.domain.model.Play;
import com.breixo.culo.domain.model.Round;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.model.game.PlayResult;
import com.breixo.culo.domain.port.input.game.PlayCardsUseCase;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The Class PlayCardsUseCaseImpl.
 */
@Component
@RequiredArgsConstructor
public class PlayCardsUseCaseImpl implements PlayCardsUseCase {

  /** The Constant RULE_ENGINE. */
  private static final RuleEngine RULE_ENGINE = new RuleEngine();

  /** The room persistence port. */
  private final RoomPersistencePort roomPersistencePort;

  /**
	 * Execute.
	 *
	 * @param playCardsCommand the play cards command
	 * @return the play result
	 */
  @Override
  public PlayResult execute(final PlayCardsCommand playCardsCommand) {

    final var room = this.roomPersistencePort.findByCode(playCardsCommand.roomCode())
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.ROOM_NOT_FOUND));
    final var player = room.findPlayerByClientId(playCardsCommand.clientId())
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

    final var cards = this.toCards(playCardsCommand, room, player.getId());
    final var play = Play.builder().cards(cards).build();
    final var round = room.getCurrentRound();

    if (!RULE_ENGINE.isLegal(play, round)) {
      throw new GameException(GameExceptionConstants.ILLEGAL_PLAY);
    }

    final var plin = RULE_ENGINE.isPlin(play, round);
    final var isAsOros = play.isAsOros();

    room.getHand(player.getId()).removeAll(cards);

    final var discardedQuads = room.discardQuads(player.getId());

    if (isAsOros) {
      round.reset();
    } else if (plin) {
      final var skippedPlayerId = room.getNextActivePlayerId();
      round.registerPlinPlay(play, player.getId(), skippedPlayerId);
    } else {
      round.registerPlay(play, player.getId());
    }

    final var playerOut = room.getHand(player.getId()).isEmpty();
    boolean gameEnded = false;
    if (playerOut) {
      gameEnded = room.registerPlayerOut(player.getId());
    }

    boolean roundEndedByPlin = false;
    if (gameEnded) {
      room.setPhase(GamePhase.DEALING);
    } else if (isAsOros) {
      if (playerOut) {
        room.advanceTurn(false);
      }
    } else {
      room.advanceTurn(plin);
      if (plin && this.closeRoundIfOthersAllPassed(room, round)) {
        roundEndedByPlin = true;
      }
    }

    final var savedRoom = this.roomPersistencePort.save(room);
    return PlayResult.builder()
        .room(savedRoom)
        .playerId(player.getId())
        .play(play)
        .plin(plin && !isAsOros)
        .roundEnded(isAsOros || roundEndedByPlin)
        .gameEnded(gameEnded)
        .build();
  }

  /**
	 * Close round if others all passed.
	 *
	 * @param room  the room
	 * @param round the round
	 * @return true, if successful
	 */
  private boolean closeRoundIfOthersAllPassed(final Room room, final Round round) {

    final var activePlayerIds = room.getPlayerOrder().stream()
        .filter(id -> !room.isPlayerOut(id))
        .toList();
    if (!RULE_ENGINE.isRoundOver(round, activePlayerIds)) {
      return false;
    }
    room.finishRoundAndSetOpener();
    return true;
  }

  /**
	 * To cards.
	 *
	 * @param command  the command
	 * @param room     the room
	 * @param playerId the player id
	 * @return the list
	 */
  private List<Card> toCards(final PlayCardsCommand command, final Room room, final String playerId) {

    final var hand = room.getHand(playerId);
    final var cards = command.cards().stream()
        .map(input -> Card.builder().suit(input.suit()).number(input.number()).build())
        .toList();
    if (!hand.containsAll(cards)) {
      throw new GameException(GameExceptionConstants.CARDS_NOT_IN_HAND);
    }
    return cards;
  }

}
