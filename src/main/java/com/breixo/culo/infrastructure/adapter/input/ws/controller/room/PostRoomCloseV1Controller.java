package com.breixo.culo.infrastructure.adapter.input.ws.controller.room;

import com.breixo.culo.domain.port.input.room.CloseRoomUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.api.PostRoomCloseV1Api;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomCloseV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.room.PostRoomCloseV1RequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomEventPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * The Class PostRoomCloseV1Controller.
 */
@Controller
@RequiredArgsConstructor
public class PostRoomCloseV1Controller implements PostRoomCloseV1Api {

  /** The close room use case. */
  private final CloseRoomUseCase closeRoomUseCase;

  /** The post room close V 1 request mapper. */
  private final PostRoomCloseV1RequestMapper postRoomCloseV1RequestMapper;

  /** The room event publisher. */
  private final RoomEventPublisher roomEventPublisher;

  /** {@inheritDoc} */
  @Override
  @MessageMapping("/room.close")
  public ResponseEntity<Void> postRoomCloseV1(
      @Payload @Valid final PostRoomCloseV1RequestDto postRoomCloseV1RequestDto) {

    final var closeRoomCommand = this.postRoomCloseV1RequestMapper.toCloseRoomCommand(postRoomCloseV1RequestDto);
    final var roomSnapshot = this.closeRoomUseCase.execute(closeRoomCommand);
    this.roomEventPublisher.publishRoomClosed(roomSnapshot);

    return ResponseEntity.noContent().build();
  }
}
