package com.breixo.culo.infrastructure.adapter.input.ws.controller.room;

import com.breixo.culo.domain.port.input.room.JoinRoomUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomEventPublisher;
import com.breixo.culo.infrastructure.adapter.input.ws.api.PostRoomJoinV1Api;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomJoinV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.room.PostRoomJoinV1RequestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * The Class PostRoomJoinV1Controller.
 */
@Controller
@RequiredArgsConstructor
public class PostRoomJoinV1Controller implements PostRoomJoinV1Api {

  /** The join room use case. */
  private final JoinRoomUseCase joinRoomUseCase;

  /** The post room join V 1 request mapper. */
  private final PostRoomJoinV1RequestMapper postRoomJoinV1RequestMapper;

  /** The room event publisher. */
  private final RoomEventPublisher roomEventPublisher;

    /** {@inheritDoc} */
  @Override
  @MessageMapping("/room.join")
  public ResponseEntity<Void> postRoomJoinV1(
      @Payload @Valid final PostRoomJoinV1RequestDto postRoomJoinV1RequestDto) {
 
    final var joinRoomCommand = this.postRoomJoinV1RequestMapper.toJoinRoomCommand(postRoomJoinV1RequestDto);

    final var roomJoinResult = this.joinRoomUseCase.execute(joinRoomCommand);

    this.roomEventPublisher.publishJoinResult(roomJoinResult);

    return ResponseEntity.noContent().build();
  }
}
