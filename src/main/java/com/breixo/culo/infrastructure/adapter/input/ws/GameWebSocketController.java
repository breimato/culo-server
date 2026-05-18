package com.breixo.culo.infrastructure.adapter.input.ws;

import com.breixo.culo.domain.command.game.CuloSwapInitiateCommand;
import com.breixo.culo.domain.command.game.CuloSwapVoteCommand;
import com.breixo.culo.domain.command.game.DealCardsCommand;
import com.breixo.culo.domain.command.game.ExchangeGiveCommand;
import com.breixo.culo.domain.command.game.PassCommand;
import com.breixo.culo.domain.exception.CuloException;
import com.breixo.culo.domain.port.input.game.CuloSwapInitiateUseCase;
import com.breixo.culo.domain.port.input.game.CuloSwapVoteUseCase;
import com.breixo.culo.domain.port.input.game.DealCardsUseCase;
import com.breixo.culo.domain.port.input.game.ExchangeGiveUseCase;
import com.breixo.culo.domain.port.input.game.PassUseCase;
import com.breixo.culo.domain.port.input.game.PlayCardsUseCase;
import com.breixo.culo.domain.port.input.room.CreateRoomUseCase;
import com.breixo.culo.domain.port.input.room.JoinRoomUseCase;
import com.breixo.culo.domain.port.input.room.StartGameUseCase;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CuloSwapInitiateRequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CuloSwapVoteRequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CreateRoomRequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.DealingConfirmRequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.ExchangeGiveRequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.JoinRoomRequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PassRequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayCardsRequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.StartGameRequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.CreateRoomRequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.ExchangeGiveRequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.JoinRoomRequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.PlayCardsRequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.StartGameRequestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

  /** The create room use case. */
  private final CreateRoomUseCase createRoomUseCase;

  /** The join room use case. */
  private final JoinRoomUseCase joinRoomUseCase;

  /** The start game use case. */
  private final StartGameUseCase startGameUseCase;

  /** The deal cards use case. */
  private final DealCardsUseCase dealCardsUseCase;

  /** The play cards use case. */
  private final PlayCardsUseCase playCardsUseCase;

  /** The pass use case. */
  private final PassUseCase passUseCase;

  /** The exchange give use case. */
  private final ExchangeGiveUseCase exchangeGiveUseCase;

  /** The culo swap initiate use case. */
  private final CuloSwapInitiateUseCase culoSwapInitiateUseCase;

  /** The culo swap vote use case. */
  private final CuloSwapVoteUseCase culoSwapVoteUseCase;

  /** The room event publisher. */
  private final RoomEventPublisher roomEventPublisher;

  /** The room persistence port. */
  private final RoomPersistencePort roomPersistencePort;

  /** The create room request mapper. */
  private final CreateRoomRequestMapper createRoomRequestMapper;

  /** The join room request mapper. */
  private final JoinRoomRequestMapper joinRoomRequestMapper;

  /** The start game request mapper. */
  private final StartGameRequestMapper startGameRequestMapper;

  /** The play cards request mapper. */
  private final PlayCardsRequestMapper playCardsRequestMapper;

  /** The exchange give request mapper. */
  private final ExchangeGiveRequestMapper exchangeGiveRequestMapper;

  // ─── Room ────────────────────────────────────────────────────────────────

  @MessageMapping("/room.create")
  public void createRoom(@Payload final CreateRoomRequestDto createRoomRequestDto) {
    try {
      final var createRoomCommand = this.createRoomRequestMapper.toCreateRoomCommand(createRoomRequestDto);
      final var roomJoinResult = this.createRoomUseCase.execute(createRoomCommand);
      this.roomEventPublisher.publishJoinResult(roomJoinResult);
    } catch (final CuloException culoException) {
      log.warn("Error creating room: {}", culoException.getMessage());
      this.roomEventPublisher.publishErrorToClient(createRoomRequestDto.getClientId(), culoException);
    }
  }

  @MessageMapping("/room.join")
  public void joinRoom(@Payload final JoinRoomRequestDto joinRoomRequestDto) {
    try {
      final var joinRoomCommand = this.joinRoomRequestMapper.toJoinRoomCommand(joinRoomRequestDto);
      final var roomJoinResult = this.joinRoomUseCase.execute(joinRoomCommand);
      this.roomEventPublisher.publishJoinResult(roomJoinResult);
    } catch (final CuloException culoException) {
      log.warn("Error joining room: {}", culoException.getMessage());
      this.roomEventPublisher.publishErrorToClient(joinRoomRequestDto.getClientId(), culoException);
    }
  }

  @MessageMapping("/room.start")
  public void startGame(@Payload final StartGameRequestDto startGameRequestDto) {
    try {
      final var startGameCommand = this.startGameRequestMapper.toStartGameCommand(startGameRequestDto);
      final var room = this.startGameUseCase.execute(startGameCommand);
      this.roomEventPublisher.publishRoomState(room);
    } catch (final CuloException culoException) {
      log.warn("Error starting game: {}", culoException.getMessage());
      this.roomEventPublisher.publishErrorToClient(startGameRequestDto.getClientId(), culoException);
      this.resolvePlayerId(startGameRequestDto.getClientId(), startGameRequestDto.getRoomCode())
          .ifPresent(id -> this.roomEventPublisher.publishError(id, culoException));
    }
  }

  // ─── Game ────────────────────────────────────────────────────────────────

  @MessageMapping("/dealing.confirm")
  public void dealCards(@Payload final DealingConfirmRequestDto dealingConfirmRequestDto) {
    try {
      final var dealCardsCommand = DealCardsCommand.builder()
          .clientId(dealingConfirmRequestDto.getClientId())
          .roomCode(dealingConfirmRequestDto.getRoomCode())
          .build();
      final var room = this.dealCardsUseCase.execute(dealCardsCommand);
      this.roomEventPublisher.publishRoomState(room);
      this.roomEventPublisher.publishAllHands(room);
    } catch (final CuloException culoException) {
      log.warn("Error dealing cards: {}", culoException.getMessage());
      this.roomEventPublisher.publishErrorToClient(dealingConfirmRequestDto.getClientId(), culoException);
    }
  }

  @MessageMapping("/game.play")
  public void playCards(@Payload final PlayCardsRequestDto playCardsRequestDto) {
    try {
      final var playCardsCommand = this.playCardsRequestMapper.toPlayCardsCommand(playCardsRequestDto);
      final var playResult = this.playCardsUseCase.execute(playCardsCommand);
      final var room = playResult.room();
      this.roomEventPublisher.publishPlayMade(room, playResult);
      this.roomEventPublisher.publishRoomState(room);
      if (playResult.gameEnded()) {
        this.roomEventPublisher.publishGameEnded(room);
        this.roomEventPublisher.publishAllHands(room);
      } else {
        this.roomEventPublisher.publishHandUpdate(room, playResult.playerId());
        if (playResult.roundEnded()) {
          this.roomEventPublisher.publishRoundEnded(room, room.getCurrentPlayerId());
        }
        this.roomEventPublisher.publishTurnChanged(room);
      }
    } catch (final CuloException culoException) {
      log.warn("Error playing cards: {}", culoException.getMessage());
      this.roomEventPublisher.publishErrorToClient(playCardsRequestDto.getClientId(), culoException);
    }
  }

  @MessageMapping("/game.pass")
  public void pass(@Payload final PassRequestDto passRequestDto) {
    try {
      final var passCommand = PassCommand.builder()
          .clientId(passRequestDto.getClientId())
          .roomCode(passRequestDto.getRoomCode())
          .build();
      final var passResult = this.passUseCase.execute(passCommand);
      final var room = passResult.room();
      this.roomEventPublisher.publishRoomState(room);
      if (passResult.roundEnded()) {
        this.roomEventPublisher.publishRoundEnded(room, room.getCurrentPlayerId());
      }
      this.roomEventPublisher.publishTurnChanged(room);
    } catch (final CuloException culoException) {
      log.warn("Error passing: {}", culoException.getMessage());
      this.roomEventPublisher.publishErrorToClient(passRequestDto.getClientId(), culoException);
    }
  }

  // ─── Exchange ─────────────────────────────────────────────────────────────

  @MessageMapping("/exchange.give")
  public void exchangeGive(@Payload final ExchangeGiveRequestDto exchangeGiveRequestDto) {
    try {
      final var exchangeGiveCommand = this.exchangeGiveRequestMapper.toExchangeGiveCommand(exchangeGiveRequestDto);
      final var room = this.exchangeGiveUseCase.execute(exchangeGiveCommand);
      this.roomEventPublisher.publishRoomState(room);
      room.getPlayers().forEach(player ->
          this.roomEventPublisher.publishHandUpdate(room, player.getId()));
    } catch (final CuloException culoException) {
      log.warn("Error in exchange: {}", culoException.getMessage());
      this.roomEventPublisher.publishErrorToClient(exchangeGiveRequestDto.getClientId(), culoException);
    }
  }

  // ─── CuloSwap ─────────────────────────────────────────────────────────────

  @MessageMapping("/culoSwap.initiate")
  public void culoSwapInitiate(@Payload final CuloSwapInitiateRequestDto culoSwapInitiateRequestDto) {
    try {
      final var command = CuloSwapInitiateCommand.builder()
          .clientId(culoSwapInitiateRequestDto.getClientId())
          .roomCode(culoSwapInitiateRequestDto.getRoomCode())
          .targetPlayerId(culoSwapInitiateRequestDto.getTargetPlayerId())
          .build();
      final var room = this.culoSwapInitiateUseCase.execute(command);
      this.roomEventPublisher.publishRoomState(room);
      this.roomEventPublisher.publishCuloSwapRequest(room);
    } catch (final CuloException culoException) {
      log.warn("Error initiating culo swap: {}", culoException.getMessage());
      this.roomEventPublisher.publishErrorToClient(culoSwapInitiateRequestDto.getClientId(), culoException);
    }
  }

  @MessageMapping("/culoSwap.vote")
  public void culoSwapVote(@Payload final CuloSwapVoteRequestDto culoSwapVoteRequestDto) {
    try {
      final var command = CuloSwapVoteCommand.builder()
          .clientId(culoSwapVoteRequestDto.getClientId())
          .roomCode(culoSwapVoteRequestDto.getRoomCode())
          .accept(culoSwapVoteRequestDto.getAccept())
          .build();
      final var voteResult = this.culoSwapVoteUseCase.execute(command);
      final var room = voteResult.room();
      this.roomEventPublisher.publishRoomState(room);
      if (voteResult.completed()) {
        this.roomEventPublisher.publishCuloSwapResult(room, voteResult.accepted());
        if (voteResult.accepted()) {
          this.roomEventPublisher.publishAllHands(room);
        }
      }
    } catch (final CuloException culoException) {
      log.warn("Error voting culo swap: {}", culoException.getMessage());
      this.roomEventPublisher.publishErrorToClient(culoSwapVoteRequestDto.getClientId(), culoException);
    }
  }

  // ─── Helpers ─────────────────────────────────────────────────────────────

  private Optional<String> resolvePlayerId(final String clientId, final String roomCode) {
    if (StringUtils.isBlank(roomCode)) {
      return Optional.empty();
    }
    return this.roomPersistencePort.findByCode(roomCode)
        .flatMap(room -> room.findPlayerByClientId(clientId))
        .map(player -> player.getId());
  }
}
