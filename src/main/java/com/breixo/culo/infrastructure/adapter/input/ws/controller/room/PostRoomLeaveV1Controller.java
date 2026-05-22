package com.breixo.culo.infrastructure.adapter.input.ws.controller.room;

import com.breixo.culo.domain.port.input.room.LeaveRoomUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.api.PostRoomLeaveV1Api;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomLeaveV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.room.PostRoomLeaveV1RequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomEventPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/** STOMP controller for leaving a room. */
@Controller
@RequiredArgsConstructor
public class PostRoomLeaveV1Controller implements PostRoomLeaveV1Api {

  /** The leave room use case. */
  private final LeaveRoomUseCase leaveRoomUseCase;

  /** The post room leave V 1 request mapper. */
  private final PostRoomLeaveV1RequestMapper postRoomLeaveV1RequestMapper;

  /** The room event publisher. */
  private final RoomEventPublisher roomEventPublisher;

  /** {@inheritDoc} */
  @Override
  @MessageMapping("/room.leave")
  public ResponseEntity<Void> postRoomLeaveV1(
      @Payload @Valid final PostRoomLeaveV1RequestDto postRoomLeaveV1RequestDto) {

    final var leaveRoomCommand = this.postRoomLeaveV1RequestMapper.toLeaveRoomCommand(postRoomLeaveV1RequestDto);
    final var roomOptional = this.leaveRoomUseCase.execute(leaveRoomCommand);

    if (roomOptional.isPresent()) {
      this.roomEventPublisher.publishRoomState(roomOptional.get());
    }

    return ResponseEntity.noContent().build();
  }
}
