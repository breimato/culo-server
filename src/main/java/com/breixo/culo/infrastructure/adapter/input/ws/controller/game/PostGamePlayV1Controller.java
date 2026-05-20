package com.breixo.culo.infrastructure.adapter.input.ws.controller.game;

import com.breixo.culo.domain.port.input.game.PlayCardsUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.RoomAckCoordinator;
import com.breixo.culo.infrastructure.adapter.input.ws.RoomEventPublisher;
import com.breixo.culo.infrastructure.adapter.input.ws.api.PostGamePlayV1Api;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostGamePlayV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.PostGamePlayV1RequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.support.PlayFollowUpSupport;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * The Class PostGamePlayV1Controller.
 */
@Controller
@RequiredArgsConstructor
public class PostGamePlayV1Controller implements PostGamePlayV1Api {

  /** The play cards use case. */
  private final PlayCardsUseCase playCardsUseCase;

  /** The post game play V 1 request mapper. */
  private final PostGamePlayV1RequestMapper postGamePlayV1RequestMapper;

  /** The room event publisher. */
  private final RoomEventPublisher roomEventPublisher;

  /** The room ack coordinator. */
  private final RoomAckCoordinator roomAckCoordinator;

  /** The play follow up support. */
  private final PlayFollowUpSupport playFollowUpSupport;

    /** {@inheritDoc} */
  @Override
  @MessageMapping("/game.play")
  public ResponseEntity<Void> postGamePlayV1(
      @Payload @Valid final PostGamePlayV1RequestDto postGamePlayV1RequestDto) {
 
    final var playCardsCommand = this.postGamePlayV1RequestMapper.toPlayCardsCommand(postGamePlayV1RequestDto);

    final var playResult = this.playCardsUseCase.execute(playCardsCommand);

    final var room = playResult.room();
    final var playFollowUpTask = this.playFollowUpSupport.playFollowUpTask(room.getCode(), playResult);
    final var eventId = this.roomAckCoordinator.awaitAllConnected(room, playFollowUpTask);

    this.roomEventPublisher.publishPlayMade(room, playResult, eventId);

    return ResponseEntity.noContent().build();
  }
}
