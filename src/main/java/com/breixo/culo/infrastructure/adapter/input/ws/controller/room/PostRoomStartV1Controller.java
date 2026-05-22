package com.breixo.culo.infrastructure.adapter.input.ws.controller.room;

import com.breixo.culo.domain.port.input.room.StartGameUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomEventPublisher;
import com.breixo.culo.infrastructure.adapter.input.ws.api.PostRoomStartV1Api;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomStartV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.room.PostRoomStartV1RequestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * The Class PostRoomStartV1Controller.
 */
@Controller
@RequiredArgsConstructor
public class PostRoomStartV1Controller implements PostRoomStartV1Api {

  /** The start game use case. */
  private final StartGameUseCase startGameUseCase;

  /** The post room start V 1 request mapper. */
  private final PostRoomStartV1RequestMapper postRoomStartV1RequestMapper;

  /** The room event publisher. */
  private final RoomEventPublisher roomEventPublisher;

    /** {@inheritDoc} */
  @Override
  @MessageMapping("/room.start")
  public ResponseEntity<Void> postRoomStartV1(
      @Payload @Valid final PostRoomStartV1RequestDto postRoomStartV1RequestDto) {
 
    final var startGameCommand = this.postRoomStartV1RequestMapper.toStartGameCommand(postRoomStartV1RequestDto);

    final var room = this.startGameUseCase.execute(startGameCommand);

    this.roomEventPublisher.publishRoomState(room);

    return ResponseEntity.noContent().build();
  }
}
