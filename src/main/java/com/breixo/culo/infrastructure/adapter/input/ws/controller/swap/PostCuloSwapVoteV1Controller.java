package com.breixo.culo.infrastructure.adapter.input.ws.controller.swap;

import com.breixo.culo.domain.port.input.swap.CuloSwapVoteUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.api.PostCuloSwapVoteV1Api;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostCuloSwapVoteV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.swap.PostCuloSwapVoteV1RequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.support.swap.CuloSwapVoteFollowUpSupport;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * The Class PostCuloSwapVoteV1Controller.
 */
@Controller
@RequiredArgsConstructor
public class PostCuloSwapVoteV1Controller implements PostCuloSwapVoteV1Api {

  /** The culo swap vote use case. */
  private final CuloSwapVoteUseCase culoSwapVoteUseCase;

  /** The post culo swap vote V 1 request mapper. */
  private final PostCuloSwapVoteV1RequestMapper postCuloSwapVoteV1RequestMapper;

  /** The culo swap vote follow up support. */
  private final CuloSwapVoteFollowUpSupport culoSwapVoteFollowUpSupport;

  /** {@inheritDoc} */
  @Override
  @MessageMapping("/culoSwap.vote")
  public ResponseEntity<Void> postCuloSwapVoteV1(
      @Payload @Valid final PostCuloSwapVoteV1RequestDto postCuloSwapVoteV1RequestDto) {

    final var culoSwapVoteCommand = this.postCuloSwapVoteV1RequestMapper
        .toCuloSwapVoteCommand(postCuloSwapVoteV1RequestDto);

    final var culoSwapVoteResult = this.culoSwapVoteUseCase.execute(culoSwapVoteCommand);

    this.culoSwapVoteFollowUpSupport.publishVoteFollowUp(culoSwapVoteResult);

    return ResponseEntity.noContent().build();
  }
}
