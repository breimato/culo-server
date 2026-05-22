package com.breixo.culo.infrastructure.adapter.input.ws.controller.room;

import com.breixo.culo.domain.port.input.room.CreateRoomUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomEventPublisher;
import com.breixo.culo.infrastructure.adapter.input.ws.api.PostRoomCreateV1Api;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomCreateV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.room.PostRoomCreateV1RequestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * The Class PostRoomCreateV1Controller.
 */
@Controller
@RequiredArgsConstructor
public class PostRoomCreateV1Controller implements PostRoomCreateV1Api {

  /** The create room use case. */
  private final CreateRoomUseCase createRoomUseCase;

  /** The post room create V 1 request mapper. */
  private final PostRoomCreateV1RequestMapper postRoomCreateV1RequestMapper;

  /** The room event publisher. */
  private final RoomEventPublisher roomEventPublisher;

    /** {@inheritDoc} */
  @Override
  @MessageMapping("/room.create")
  public ResponseEntity<Void> postRoomCreateV1(
      @Payload @Valid final PostRoomCreateV1RequestDto postRoomCreateV1RequestDto) {
 
    final var createRoomCommand = this.postRoomCreateV1RequestMapper
        .toCreateRoomCommand(postRoomCreateV1RequestDto);

    final var roomJoinResult = this.createRoomUseCase.execute(createRoomCommand);

    this.roomEventPublisher.publishJoinResult(roomJoinResult);

    return ResponseEntity.noContent().build();
  }
}
