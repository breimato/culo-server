package com.breixo.culo.infrastructure.adapter.input.ws.controller.swap;

import com.breixo.culo.domain.port.input.swap.CuloSwapInitiateUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomEventPublisher;
import com.breixo.culo.infrastructure.adapter.input.ws.api.PostCuloSwapInitiateV1Api;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostCuloSwapInitiateV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.swap.PostCuloSwapInitiateV1RequestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * The Class PostCuloSwapInitiateV1Controller.
 */
@Controller
@RequiredArgsConstructor
public class PostCuloSwapInitiateV1Controller implements PostCuloSwapInitiateV1Api {

  /** The culo swap initiate use case. */
  private final CuloSwapInitiateUseCase culoSwapInitiateUseCase;

  /** The post culo swap initiate V 1 request mapper. */
  private final PostCuloSwapInitiateV1RequestMapper postCuloSwapInitiateV1RequestMapper;

  /** The room event publisher. */
  private final RoomEventPublisher roomEventPublisher;

  /** {@inheritDoc} */
  @Override
  @MessageMapping("/culoSwap.initiate")
  public ResponseEntity<Void> postCuloSwapInitiateV1(
      @Payload @Valid final PostCuloSwapInitiateV1RequestDto postCuloSwapInitiateV1RequestDto) {

    final var culoSwapInitiateCommand = this.postCuloSwapInitiateV1RequestMapper
        .toCuloSwapInitiateCommand(postCuloSwapInitiateV1RequestDto);

    final var room = this.culoSwapInitiateUseCase.execute(culoSwapInitiateCommand);

    this.roomEventPublisher.publishRoomState(room);
    this.roomEventPublisher.publishCuloSwapRequest(room);

    return ResponseEntity.noContent().build();
  }
}
