package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.model.CardRank;
import com.breixo.culo.domain.model.Player;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardRankNameDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayerDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.RoomStateDto;
import com.breixo.culo.infrastructure.mapper.GamePhaseMapper;
import com.breixo.culo.infrastructure.mapper.PlayerRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoomStateDtoMapper {

  /** The game phase mapper. */
  private final GamePhaseMapper gamePhaseMapper;

  /** The player role mapper. */
  private final PlayerRoleMapper playerRoleMapper;

  /** The card dto mapper. */
  private final CardDtoMapper cardDtoMapper;

  public RoomStateDto toRoomStateDto(final Room room) {
    final var round = room.getCurrentRound();
    final var lastRankName = toCardRankNameDto(round == null ? null : round.getLastRank());
    final var players = room.getPlayers().stream()
        .map(player -> this.toPlayerDto(player, room))
        .toList();

    return RoomStateDto.builder()
        .roomCode(room.getCode())
        .hostPlayerId(room.getHostPlayerId())
        .phase(this.gamePhaseMapper.toGamePhaseDto(room.getPhase()))
        .players(players)
        .currentPlayerId(room.getCurrentPlayerId())
        .roundRequirement(round == null ? 0 : round.getRequirement())
        .lastRankName(lastRankName)
        .lastPlayedCards(round == null
            ? List.of()
            : this.cardDtoMapper.toCardDtoList(round.getLastPlayedCards()))
        .culoSwapInitiatorId(room.getCuloSwapInitiatorId())
        .culoSwapTargetId(room.getCuloSwapTargetId())
        .build();
  }

  private PlayerDto toPlayerDto(final Player player, final Room room) {
    final var hand = room.getHand(player.getId());
    return PlayerDto.builder()
        .id(player.getId())
        .nick(player.getNick())
        .connected(player.isConnected())
        .role(this.playerRoleMapper.toPlayerRoleDto(player.getRole()))
        .cardCount(hand.size())
        .build();
  }

  private CardRankNameDto toCardRankNameDto(final CardRank cardRank) {
    if (cardRank == null) {
      return null;
    }
    return CardRankNameDto.valueOf(cardRank.name());
  }
}
