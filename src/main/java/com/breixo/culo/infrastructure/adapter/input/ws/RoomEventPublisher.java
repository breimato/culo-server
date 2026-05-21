package com.breixo.culo.infrastructure.adapter.input.ws;

import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.exception.CuloException;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.card.enums.Suit;
import com.breixo.culo.domain.model.play.PlayResult;
import com.breixo.culo.domain.model.quad.QuadDiscardEvent;
import com.breixo.culo.domain.model.room.RoomJoinResult;
import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.ApiErrorV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardRankNameV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CuloSwapRequestV1ResponseDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CuloSwapResultV1ResponseDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.GameEndedV1ResponseDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.HandUpdateV1ResponseDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.JoinedRoomV1ResponseDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayMadeV1ResponseDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayerRoleV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.QuadDiscardedV1ResponseDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.RankingEntryV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.RoomStateV1ResponseDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.RoundEndedV1ResponseDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.TurnChangedV1ResponseDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.CardV1DtoMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.JoinedRoomV1ResponseMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.RoomStateV1ResponseMapper;
import com.breixo.culo.infrastructure.config.WsDestinationConstants;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * The Class RoomEventPublisher.
 */
@Component
@RequiredArgsConstructor
public class RoomEventPublisher {

  /** The simp messaging template. */
  private final SimpMessagingTemplate simpMessagingTemplate;

  /** The joined room V 1 response mapper. */
  private final JoinedRoomV1ResponseMapper joinedRoomV1ResponseMapper;

  /** The room state V 1 response mapper. */
  private final RoomStateV1ResponseMapper roomStateV1ResponseMapper;

  /** The card V 1 dto mapper. */
  private final CardV1DtoMapper cardV1DtoMapper;

  /** The room service. */
  private final PlayerLookupService playerLookupService;

  // ─── Room / lobby events ─────────────────────────────────────────────────

  /**
	 * Publish join result.
	 *
	 * @param roomJoinResult the room join result
	 */
  public void publishJoinResult(final RoomJoinResult roomJoinResult) {

    final var joinedRoomV1ResponseDto = this.joinedRoomV1ResponseMapper
        .toJoinedRoomV1ResponseDto(roomJoinResult);
    final var clientId = this.playerLookupService.findPlayerById(roomJoinResult.room(), roomJoinResult.playerId())
        .map(player -> player.clientId())
        .orElseThrow();
    this.publishJoinedRoom(roomJoinResult.playerId(), joinedRoomV1ResponseDto);
    this.publishJoinedRoomToClient(clientId, joinedRoomV1ResponseDto);
    this.publishRoomState(roomJoinResult.room());
  }

  /**
	 * Publish room state.
	 *
	 * @param room the room
	 */
  public void publishRoomState(final Room room) {

    final var roomStateV1ResponseDto = this.roomStateV1ResponseMapper.toRoomStateV1ResponseDto(room);
    final var destination = WsDestinationConstants.roomTopic(room.roomLobby().code())
        + WsDestinationConstants.ROOM_STATE_SUFFIX;
    this.simpMessagingTemplate.convertAndSend(destination, roomStateV1ResponseDto);
  }

  /**
	 * Publish joined room.
	 *
	 * @param playerId                the player id
	 * @param joinedRoomV1ResponseDto the joined room V 1 response dto
	 */
  public void publishJoinedRoom(final String playerId, final JoinedRoomV1ResponseDto joinedRoomV1ResponseDto) {

    final var destination = WsDestinationConstants.playerQueue(playerId)
        + WsDestinationConstants.JOINED_ROOM_SUFFIX;
    this.simpMessagingTemplate.convertAndSend(destination, joinedRoomV1ResponseDto);
  }

  /**
	 * Publish joined room to client.
	 *
	 * @param clientId                the client id
	 * @param joinedRoomV1ResponseDto the joined room V 1 response dto
	 */
  public void publishJoinedRoomToClient(final String clientId, final JoinedRoomV1ResponseDto joinedRoomV1ResponseDto) {

    final var destination = WsDestinationConstants.clientTopic(clientId)
        + WsDestinationConstants.JOINED_ROOM_SUFFIX;
    this.simpMessagingTemplate.convertAndSend(destination, joinedRoomV1ResponseDto);
  }

  // ─── Game events ─────────────────────────────────────────────────────────

  /**
	 * Publish hand update.
	 *
	 * @param room     the room
	 * @param playerId the player id
	 */
  public void publishHandUpdate(final Room room, final String playerId) {

    final var hand = room.gameSession().hands().getOrDefault(playerId, List.of());
    final var handUpdateV1ResponseDto = HandUpdateV1ResponseDto.builder()
        .cards(this.cardV1DtoMapper.toCardV1DtoList(hand))
        .build();
    final var destination = WsDestinationConstants.clientTopic(
        this.playerLookupService.findPlayerById(room, playerId).orElseThrow().clientId())
        + "/handUpdate";
    this.simpMessagingTemplate.convertAndSend(destination, handUpdateV1ResponseDto);
  }

  /**
	 * Publish all hands.
	 *
	 * @param room the room
	 */
  public void publishAllHands(final Room room) {
    room.roomLobby().players().forEach(player ->
        this.publishHandUpdate(room, player.id()));
  }

  /**
	 * Publish play made.
	 *
	 * @param room       the room
	 * @param playResult the play result
	 * @param eventId    the event id
	 */
  public void publishPlayMade(final Room room, final PlayResult playResult, final String eventId) {

    final var cards = this.cardV1DtoMapper.toCardV1DtoList(playResult.play().cards());
    final var leadCard = playResult.play().cards().getFirst();
    final var isAsOros = Integer.valueOf(1).equals(playResult.play().cards().size())
        && Integer.valueOf(1).equals(leadCard.number())
        && Suit.OROS.equals(leadCard.suit());

    final var playMadeV1ResponseDto = PlayMadeV1ResponseDto.builder()
        .eventId(eventId)
        .playEpoch(room.gameSession().playEpoch())
        .playerId(playResult.playerId())
        .cards(cards)
        .plin(playResult.plin())
        .isAsOros(isAsOros)
        .build();

    final var destination = WsDestinationConstants.roomTopic(room.roomLobby().code()) + "/playMade";
    this.simpMessagingTemplate.convertAndSend(destination, playMadeV1ResponseDto);
  }

  /**
	 * Publish round ended.
	 *
	 * @param room           the room
	 * @param winnerPlayerId the winner player id
	 */
  public void publishRoundEnded(final Room room, final String winnerPlayerId) {

    final var roundEndedV1ResponseDto = RoundEndedV1ResponseDto.builder()
        .winnerPlayerId(winnerPlayerId)
        .build();
    final var destination = WsDestinationConstants.roomTopic(room.roomLobby().code()) + "/roundEnded";
    this.simpMessagingTemplate.convertAndSend(destination, roundEndedV1ResponseDto);
  }

  /**
	 * Publish turn changed.
	 *
	 * @param room the room
	 */
  public void publishTurnChanged(final Room room) {

    final var round = room.gameSession().currentRound();
    final var lastRankName = Objects.isNull(round.lastRank()) ? null
        : CardRankNameV1Dto.valueOf(round.lastRank().name());
    final String currentPlayerId;

    if (room.gameSession().playerOrder().isEmpty()) {
      currentPlayerId = null;
    } else {
      currentPlayerId = room.gameSession().playerOrder().get(room.gameSession().currentPlayerIndex());
    }

    final var turnChangedV1ResponseDto = TurnChangedV1ResponseDto.builder()
        .currentPlayerId(currentPlayerId)
        .roundRequirement(round.requirement())
        .lastRankName(lastRankName)
        .build();
    final var destination = WsDestinationConstants.roomTopic(room.roomLobby().code()) + "/turnChanged";
    this.simpMessagingTemplate.convertAndSend(destination, turnChangedV1ResponseDto);
  }

  /**
	 * Publish game ended.
	 *
	 * @param room the room
	 */
  public void publishGameEnded(final Room room) {

    final var ranking = room.roomLobby().players().stream()
        .filter(player -> BooleanUtils.isFalse(PlayerRole.NONE.getId().equals(player.role().getId())))
        .map(player -> RankingEntryV1Dto.builder()
            .playerId(player.id())
            .nick(player.nick())
            .role(PlayerRoleV1Dto.valueOf(player.role().name()))
            .build())
        .toList();
    final var gameEndedV1ResponseDto = GameEndedV1ResponseDto.builder()
        .ranking(ranking)
        .build();
    final var destination = WsDestinationConstants.roomTopic(room.roomLobby().code()) + "/gameEnded";
    this.simpMessagingTemplate.convertAndSend(destination, gameEndedV1ResponseDto);
  }

  /**
	 * Publish culo swap request.
	 *
	 * @param room the room
	 */
  public void publishCuloSwapRequest(final Room room) {

    final var culoSwapRequestV1ResponseDto = CuloSwapRequestV1ResponseDto.builder()
        .initiatorPlayerId(room.culoSwapState().initiatorId())
        .targetPlayerId(room.culoSwapState().targetId())
        .build();
    final var destination = WsDestinationConstants.roomTopic(room.roomLobby().code()) + "/culoSwapRequest";
    this.simpMessagingTemplate.convertAndSend(destination, culoSwapRequestV1ResponseDto);
  }

  /**
	 * Publish quad discarded.
	 *
	 * @param room             the room
	 * @param quadDiscardEvent the quad discard event
	 */
  public void publishQuadDiscarded(final Room room, final QuadDiscardEvent quadDiscardEvent) {

    final var quadDiscardedV1ResponseDto = QuadDiscardedV1ResponseDto.builder()
        .playerId(quadDiscardEvent.playerId())
        .value(quadDiscardEvent.value())
        .cards(this.cardV1DtoMapper.toCardV1DtoList(quadDiscardEvent.cards()))
        .build();
    final var destination = WsDestinationConstants.roomTopic(room.roomLobby().code()) + "/quadDiscarded";
    this.simpMessagingTemplate.convertAndSend(destination, quadDiscardedV1ResponseDto);
  }

  /**
	 * Publish culo swap result.
	 *
	 * @param room     the room
	 * @param accepted the accepted
	 */
  public void publishCuloSwapResult(final Room room, final boolean accepted) {

    final var culoSwapResultV1ResponseDto = CuloSwapResultV1ResponseDto.builder()
        .accepted(accepted)
        .build();
    final var destination = WsDestinationConstants.roomTopic(room.roomLobby().code()) + "/culoSwapResult";
    this.simpMessagingTemplate.convertAndSend(destination, culoSwapResultV1ResponseDto);
  }

  // ─── Errors ──────────────────────────────────────────────────────────────

  /**
	 * Publish error.
	 *
	 * @param playerId      the player id
	 * @param culoException the culo exception
	 */
  public void publishError(final String playerId, final CuloException culoException) {

    final var apiErrorV1Dto = ApiErrorV1Dto.builder()
        .code(culoException.getCode())
        .message(culoException.getMessage())
        .build();
    final var destination = WsDestinationConstants.playerQueue(playerId)
        + WsDestinationConstants.ERROR_SUFFIX;
    this.simpMessagingTemplate.convertAndSend(destination, apiErrorV1Dto);
  }

  /**
	 * Publish error to client.
	 *
	 * @param clientId      the client id
	 * @param culoException the culo exception
	 */
  public void publishErrorToClient(final String clientId, final CuloException culoException) {

    final var apiErrorV1Dto = ApiErrorV1Dto.builder()
        .code(culoException.getCode())
        .message(culoException.getMessage())
        .build();
    final var destination = WsDestinationConstants.clientTopic(clientId)
        + WsDestinationConstants.ERROR_SUFFIX;
    this.simpMessagingTemplate.convertAndSend(destination, apiErrorV1Dto);
  }
}
