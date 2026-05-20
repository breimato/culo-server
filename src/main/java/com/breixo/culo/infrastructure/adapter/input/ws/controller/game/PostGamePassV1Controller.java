package com.breixo.culo.infrastructure.adapter.input.ws.controller.game;

import com.breixo.culo.domain.port.input.game.PassUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.api.PostGamePassV1Api;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostGamePassV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.game.PostGamePassV1RequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.support.PlayFollowUpSupport;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * The Class PostGamePassV1Controller.
 */
@Controller
@RequiredArgsConstructor
public class PostGamePassV1Controller implements PostGamePassV1Api {

  /** The pass use case. */
  private final PassUseCase passUseCase;

  /** The post game pass V 1 request mapper. */
  private final PostGamePassV1RequestMapper postGamePassV1RequestMapper;

  /** The play follow up support. */
  private final PlayFollowUpSupport playFollowUpSupport;

    /** {@inheritDoc} */
  @Override
  @MessageMapping("/game.pass")
  public ResponseEntity<Void> postGamePassV1(
      @Payload @Valid final PostGamePassV1RequestDto postGamePassV1RequestDto) {
 
    final var passCommand = this.postGamePassV1RequestMapper.toPassCommand(postGamePassV1RequestDto);

    final var passResult = this.passUseCase.execute(passCommand);

    this.playFollowUpSupport.publishPassFollowUp(passResult.room(), passResult.roundEnded());

    return ResponseEntity.noContent().build();
  }
}
