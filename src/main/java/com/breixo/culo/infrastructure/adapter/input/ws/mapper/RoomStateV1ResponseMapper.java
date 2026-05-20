package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.model.CardRank;
import com.breixo.culo.domain.model.Player;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardRankNameV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayerV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.RoomStateV1ResponseDto;
import com.breixo.culo.infrastructure.mapper.GamePhaseMapper;
import com.breixo.culo.infrastructure.mapper.PlayerRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The Class RoomStateV1ResponseMapper.
 */
@Component
@RequiredArgsConstructor
public class RoomStateV1ResponseMapper {

  /** The game phase mapper. */
  private final GamePhaseMapper gamePhaseMapper;

  /** The player role mapper. */
  private final PlayerRoleMapper playerRoleMapper;

  /** The card V 1 dto mapper. */
  private final CardV1DtoMapper cardV1DtoMapper;

  /**
	 * To room state V 1 response dto.
	 *
	 * @param room the room
	 * @return the room state V 1 response dto
	 */
  public RoomStateV1ResponseDto toRoomStateV1ResponseDto(final Room room) {
 
    final var round = room.getCurrentRound();
    final var lastRankName = this.toCardRankNameV1Dto(round == null ? null : round.getLastRank());
    final var players = room.getPlayers().stream()
        .map(player -> this.toPlayerV1Dto(player, room))
        .toList();

    return RoomStateV1ResponseDto.builder()
        .roomCode(room.getCode())
        .hostPlayerId(room.getHostPlayerId())
        .phase(this.gamePhaseMapper.toGamePhaseV1Dto(room.getPhase()))
        .players(players)
        .currentPlayerId(room.getCurrentPlayerId())
        .roundRequirement(round == null ? 0 : round.getRequirement())
        .lastRankName(lastRankName)
        .lastPlayedCards(round == null
            ? List.of()
            : this.cardV1DtoMapper.toCardV1DtoList(round.getLastPlayedCards()))
        .culoSwapInitiatorId(room.getCuloSwapInitiatorId())
        .culoSwapTargetId(room.getCuloSwapTargetId())
        .exchangeDonePlayerIds(List.copyOf(room.getExchangeDone()))
        .playEpoch(room.getPlayEpoch())
        .build();
  }

  /**
	 * To player V 1 dto.
	 *
	 * @param player the player
	 * @param room   the room
	 * @return the player V 1 dto
	 */
  private PlayerV1Dto toPlayerV1Dto(final Player player, final Room room) {
 
    final var hand = room.getHand(player.getId());
    return PlayerV1Dto.builder()
        .id(player.getId())
        .nick(player.getNick())
        .connected(player.isConnected())
        .role(this.playerRoleMapper.toPlayerRoleV1Dto(player.getRole()))
        .cardCount(hand.size())
        .build();
  }

  /**
	 * To card rank name V 1 dto.
	 *
	 * @param cardRank the card rank
	 * @return the card rank name V 1 dto
	 */
  private CardRankNameV1Dto toCardRankNameV1Dto(final CardRank cardRank) {
    if (cardRank == null) {
      return null;
    }
    return CardRankNameV1Dto.valueOf(cardRank.name());
  }
}
