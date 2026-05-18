package com.breixo.culo.application.usecase.game;

import com.breixo.culo.domain.GamePhase;
import com.breixo.culo.domain.PlayerRole;
import com.breixo.culo.domain.command.game.ExchangeGiveCommand;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.Card;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.port.input.game.ExchangeGiveUseCase;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExchangeGiveUseCaseImpl implements ExchangeGiveUseCase {

  /** The room persistence port. */
  private final RoomPersistencePort roomPersistencePort;

  @Override
  public Room execute(final ExchangeGiveCommand exchangeGiveCommand) {
    final var room = this.roomPersistencePort.findByCode(exchangeGiveCommand.roomCode())
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.ROOM_NOT_FOUND));
    final var player = room.findPlayerByClientId(exchangeGiveCommand.clientId())
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.PLAYER_NOT_IN_ROOM));
    if (!room.getPhase().equals(GamePhase.EXCHANGE)) {
      throw new GameException(GameExceptionConstants.WRONG_PHASE);
    }

    final var role = player.getRole();
    if (role == PlayerRole.GANADOR) {
      this.processGanadorGive(room, player.getId(), exchangeGiveCommand);
    } else if (role == PlayerRole.SUBCAMPEON) {
      this.processSubcampeonGive(room, player.getId(), exchangeGiveCommand);
    } else {
      throw new GameException(GameExceptionConstants.NOT_IN_EXCHANGE);
    }

    room.getExchangeDone().add(player.getId());

    if (this.isExchangeComplete(room)) {
      room.getPlayers().forEach(p -> p.setRole(PlayerRole.NONE));
      room.setPhase(GamePhase.PLAYING);
    }

    return this.roomPersistencePort.save(room);
  }

  private void processGanadorGive(
      final Room room,
      final String ganadorId,
      final ExchangeGiveCommand command) {
    final var cards = this.toCards(command, room, ganadorId);
    if (cards.size() != 2) {
      throw new GameException(GameExceptionConstants.INVALID_EXCHANGE);
    }
    final var culoId = room.getPlayerIdByRole(PlayerRole.CULO).orElseThrow();
    room.getHand(ganadorId).removeAll(cards);
    room.getHand(culoId).addAll(cards);
  }

  private void processSubcampeonGive(
      final Room room,
      final String subcampeonId,
      final ExchangeGiveCommand command) {
    final var cards = this.toCards(command, room, subcampeonId);
    if (cards.size() != 1) {
      throw new GameException(GameExceptionConstants.INVALID_EXCHANGE);
    }
    final var penultimoId = room.getPlayerIdByRole(PlayerRole.PENULTIMO).orElseThrow();
    room.getHand(subcampeonId).removeAll(cards);
    room.getHand(penultimoId).addAll(cards);
  }

  private List<Card> toCards(final ExchangeGiveCommand command, final Room room, final String playerId) {
    final var hand = room.getHand(playerId);
    final var cards = command.cards().stream()
        .map(input -> Card.builder().suit(input.suit()).number(input.number()).build())
        .toList();
    if (!hand.containsAll(cards)) {
      throw new GameException(GameExceptionConstants.CARDS_NOT_IN_HAND);
    }
    return cards;
  }

  private boolean isExchangeComplete(final Room room) {
    final var ganadorDone = room.getExchangeDone().contains(
        room.getPlayerIdByRole(PlayerRole.GANADOR).orElse(""));
    final var subcampeonRole = room.getPlayerIdByRole(PlayerRole.SUBCAMPEON);
    final var subcampeonDone = subcampeonRole.isEmpty()
        || room.getExchangeDone().contains(subcampeonRole.get());
    return ganadorDone && subcampeonDone;
  }
}
