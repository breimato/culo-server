package com.breixo.culo.infrastructure.adapter.input.ws.controller.swap;

import com.breixo.culo.domain.port.input.swap.ExchangeGiveUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.api.PostExchangeGiveV1Api;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostExchangeGiveV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.swap.PostExchangeGiveV1RequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.support.game.PlayFollowUpSupport;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * The Class PostExchangeGiveV1Controller.
 */
@Controller
@RequiredArgsConstructor
public class PostExchangeGiveV1Controller implements PostExchangeGiveV1Api {

  /** The exchange give use case. */
  private final ExchangeGiveUseCase exchangeGiveUseCase;

  /** The post exchange give V 1 request mapper. */
  private final PostExchangeGiveV1RequestMapper postExchangeGiveV1RequestMapper;

  /** The play follow up support. */
  private final PlayFollowUpSupport playFollowUpSupport;

  /** {@inheritDoc} */
  @Override
  @MessageMapping("/exchange.give")
  public ResponseEntity<Void> postExchangeGiveV1(
      @Payload @Valid final PostExchangeGiveV1RequestDto postExchangeGiveV1RequestDto) {

    final var exchangeGiveCommand = this.postExchangeGiveV1RequestMapper
        .toExchangeGiveCommand(postExchangeGiveV1RequestDto);

    final var room = this.exchangeGiveUseCase.execute(exchangeGiveCommand);

    this.playFollowUpSupport.publishExchangeFollowUp(room);

    return ResponseEntity.noContent().build();
  }
}
