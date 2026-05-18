package com.breixo.culo.application.usecase.game;

import com.breixo.culo.domain.GamePhase;
import com.breixo.culo.domain.PlayerRole;
import com.breixo.culo.domain.command.game.DealCardsCommand;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.Card;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.port.input.game.DealCardsUseCase;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DealCardsUseCaseImpl implements DealCardsUseCase {

  /** The room persistence port. */
  private final RoomPersistencePort roomPersistencePort;

  @Override
  public Room execute(final DealCardsCommand dealCardsCommand) {
    final var room = this.roomPersistencePort.findByCode(dealCardsCommand.roomCode())
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.ROOM_NOT_FOUND));
    final var player = room.findPlayerByClientId(dealCardsCommand.clientId())
        .orElseThrow(() -> new RoomException(RoomExceptionConstants.PLAYER_NOT_IN_ROOM));
    if (!room.getPhase().equals(GamePhase.DEALING)) {
      throw new GameException(GameExceptionConstants.WRONG_PHASE);
    }
    final var isCulo = player.getRole() == PlayerRole.CULO;
    final var isFirstGame = room.getLastCuloId() == null;
    final var isHost = room.isHost(player.getId());
    if (!isCulo && !isFirstGame) {
      throw new GameException(GameExceptionConstants.NOT_CULO);
    }
    if (isFirstGame && !isHost) {
      throw new RoomException(RoomExceptionConstants.NOT_HOST);
    }

    final var rolesBeforeDeal = this.captureExchangeRoles(room);
    final var needsExchange = rolesBeforeDeal.containsKey(PlayerRole.GANADOR)
        && rolesBeforeDeal.containsKey(PlayerRole.CULO);

    room.dealCards();

    if (needsExchange) {
      this.restoreExchangeRoles(room, rolesBeforeDeal);
      this.transferBestCards(
          room,
          rolesBeforeDeal.get(PlayerRole.CULO),
          rolesBeforeDeal.get(PlayerRole.GANADOR),
          2);
      room.setPhase(GamePhase.EXCHANGE);
    } else {
      room.setPhase(GamePhase.PLAYING);
    }

    return this.roomPersistencePort.save(room);
  }

  private Map<PlayerRole, String> captureExchangeRoles(final Room room) {
    final var roles = new EnumMap<PlayerRole, String>(PlayerRole.class);
    room.getPlayerIdByRole(PlayerRole.GANADOR).ifPresent(id -> roles.put(PlayerRole.GANADOR, id));
    room.getPlayerIdByRole(PlayerRole.CULO).ifPresent(id -> roles.put(PlayerRole.CULO, id));
    room.getPlayerIdByRole(PlayerRole.SUBCAMPEON).ifPresent(id -> roles.put(PlayerRole.SUBCAMPEON, id));
    room.getPlayerIdByRole(PlayerRole.PENULTIMO).ifPresent(id -> roles.put(PlayerRole.PENULTIMO, id));
    return roles;
  }

  private void restoreExchangeRoles(final Room room, final Map<PlayerRole, String> rolesByPlayer) {
    rolesByPlayer.forEach((role, playerId) ->
        room.findPlayerById(playerId).ifPresent(p -> p.setRole(role)));
  }

  private void transferBestCards(
      final Room room,
      final String giverId,
      final String receiverId,
      final int count) {
    final var giverHand = room.getHand(giverId);
    final var best = giverHand.stream()
        .sorted((a, b) -> Integer.compare(cardSortValue(b), cardSortValue(a)))
        .limit(count)
        .toList();
    giverHand.removeAll(best);
    room.getHand(receiverId).addAll(best);
  }

  private static int cardSortValue(final Card card) {
    return card.number() == 1 ? 999 : card.number();
  }
}
