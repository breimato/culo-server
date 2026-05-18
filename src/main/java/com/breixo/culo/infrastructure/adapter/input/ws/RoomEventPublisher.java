package com.breixo.culo.infrastructure.adapter.input.ws;

import com.breixo.culo.domain.PlayerRole;
import com.breixo.culo.domain.exception.CuloException;
import com.breixo.culo.domain.model.Card;
import com.breixo.culo.domain.model.Play;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.model.game.PlayResult;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardRankNameDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CuloSwapRequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CuloSwapResultDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.GameEndedDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.HandUpdateDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.JoinedRoomDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayMadeDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.RankingEntryDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.RoomStateDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.RoundEndedDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.TurnChangedDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.WsErrorDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.CardDtoMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.JoinedRoomDtoMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.RoomStateDtoMapper;
import com.breixo.culo.infrastructure.config.WsDestinationConstants;
import com.breixo.culo.domain.model.room.RoomJoinResult;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoomEventPublisher {

  /** The simp messaging template. */
  private final SimpMessagingTemplate simpMessagingTemplate;

  /** The joined room dto mapper. */
  private final JoinedRoomDtoMapper joinedRoomDtoMapper;

  /** The room state dto mapper. */
  private final RoomStateDtoMapper roomStateDtoMapper;

  /** The card dto mapper. */
  private final CardDtoMapper cardDtoMapper;

  // ─── Room / lobby events ─────────────────────────────────────────────────

  public void publishJoinResult(final RoomJoinResult roomJoinResult) {
    final var joinedRoomDto = this.joinedRoomDtoMapper.toJoinedRoomDto(roomJoinResult);
    final var clientId = roomJoinResult.room().findPlayerById(roomJoinResult.playerId())
        .map(player -> player.getClientId())
        .orElseThrow();
    this.publishJoinedRoom(roomJoinResult.playerId(), joinedRoomDto);
    this.publishJoinedRoomToClient(clientId, joinedRoomDto);
    this.publishRoomState(roomJoinResult.room());
  }

  public void publishRoomState(final Room room) {
    final var roomStateDto = this.roomStateDtoMapper.toRoomStateDto(room);
    final var destination = WsDestinationConstants.roomTopic(room.getCode())
        + WsDestinationConstants.ROOM_STATE_SUFFIX;
    this.simpMessagingTemplate.convertAndSend(destination, roomStateDto);
  }

  public void publishJoinedRoom(final String playerId, final JoinedRoomDto joinedRoomDto) {
    final var destination = WsDestinationConstants.playerQueue(playerId)
        + WsDestinationConstants.JOINED_ROOM_SUFFIX;
    this.simpMessagingTemplate.convertAndSend(destination, joinedRoomDto);
  }

  public void publishJoinedRoomToClient(final String clientId, final JoinedRoomDto joinedRoomDto) {
    final var destination = WsDestinationConstants.clientTopic(clientId)
        + WsDestinationConstants.JOINED_ROOM_SUFFIX;
    this.simpMessagingTemplate.convertAndSend(destination, joinedRoomDto);
  }

  // ─── Game events ─────────────────────────────────────────────────────────

  public void publishHandUpdate(final Room room, final String playerId) {
    final var hand = room.getHand(playerId);
    final var handUpdateDto = HandUpdateDto.builder()
        .cards(this.cardDtoMapper.toCardDtoList(hand))
        .build();
    final var destination = WsDestinationConstants.clientTopic(
        room.findPlayerById(playerId).orElseThrow().getClientId())
        + "/handUpdate";
    this.simpMessagingTemplate.convertAndSend(destination, handUpdateDto);
  }

  public void publishAllHands(final Room room) {
    room.getPlayers().forEach(player ->
        this.publishHandUpdate(room, player.getId()));
  }

  public void publishPlayMade(final Room room, final PlayResult playResult) {
    final var cards = this.cardDtoMapper.toCardDtoList(playResult.play().cards());
    final var playMadeDto = PlayMadeDto.builder()
        .playerId(playResult.playerId())
        .cards(cards)
        .plin(playResult.plin())
        .isAsOros(playResult.play().isAsOros())
        .build();
    final var destination = WsDestinationConstants.roomTopic(room.getCode()) + "/playMade";
    this.simpMessagingTemplate.convertAndSend(destination, playMadeDto);
  }

  public void publishRoundEnded(final Room room, final String winnerPlayerId) {
    final var roundEndedDto = RoundEndedDto.builder()
        .winnerPlayerId(winnerPlayerId)
        .build();
    final var destination = WsDestinationConstants.roomTopic(room.getCode()) + "/roundEnded";
    this.simpMessagingTemplate.convertAndSend(destination, roundEndedDto);
  }

  public void publishTurnChanged(final Room room) {
    final var round = room.getCurrentRound();
    final var lastRankName = round.getLastRank() == null ? null
        : CardRankNameDto.valueOf(round.getLastRank().name());
    final var turnChangedDto = TurnChangedDto.builder()
        .currentPlayerId(room.getCurrentPlayerId())
        .roundRequirement(round.getRequirement())
        .lastRankName(lastRankName)
        .build();
    final var destination = WsDestinationConstants.roomTopic(room.getCode()) + "/turnChanged";
    this.simpMessagingTemplate.convertAndSend(destination, turnChangedDto);
  }

  public void publishGameEnded(final Room room) {
    final var ranking = room.getPlayers().stream()
        .filter(player -> !player.getRole().equals(PlayerRole.NONE))
        .map(player -> RankingEntryDto.builder()
            .playerId(player.getId())
            .nick(player.getNick())
            .role(com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayerRoleDto
                .valueOf(player.getRole().name()))
            .build())
        .toList();
    final var gameEndedDto = GameEndedDto.builder()
        .ranking(ranking)
        .build();
    final var destination = WsDestinationConstants.roomTopic(room.getCode()) + "/gameEnded";
    this.simpMessagingTemplate.convertAndSend(destination, gameEndedDto);
  }

  public void publishCuloSwapRequest(final Room room) {
    final var culoSwapRequestDto = CuloSwapRequestDto.builder()
        .initiatorPlayerId(room.getCuloSwapInitiatorId())
        .targetPlayerId(room.getCuloSwapTargetId())
        .build();
    final var destination = WsDestinationConstants.roomTopic(room.getCode()) + "/culoSwapRequest";
    this.simpMessagingTemplate.convertAndSend(destination, culoSwapRequestDto);
  }

  public void publishCuloSwapResult(final Room room, final boolean accepted) {
    final var culoSwapResultDto = CuloSwapResultDto.builder()
        .accepted(accepted)
        .build();
    final var destination = WsDestinationConstants.roomTopic(room.getCode()) + "/culoSwapResult";
    this.simpMessagingTemplate.convertAndSend(destination, culoSwapResultDto);
  }

  // ─── Errors ──────────────────────────────────────────────────────────────

  public void publishError(final String playerId, final CuloException culoException) {
    final var wsErrorDto = WsErrorDto.builder()
        .code(culoException.getCode())
        .message(culoException.getMessage())
        .build();
    final var destination = WsDestinationConstants.playerQueue(playerId)
        + WsDestinationConstants.ERROR_SUFFIX;
    this.simpMessagingTemplate.convertAndSend(destination, wsErrorDto);
  }

  public void publishErrorToClient(final String clientId, final CuloException culoException) {
    final var wsErrorDto = WsErrorDto.builder()
        .code(culoException.getCode())
        .message(culoException.getMessage())
        .build();
    final var destination = WsDestinationConstants.clientTopic(clientId)
        + WsDestinationConstants.ERROR_SUFFIX;
    this.simpMessagingTemplate.convertAndSend(destination, wsErrorDto);
  }
}
