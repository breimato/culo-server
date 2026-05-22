package com.breixo.culo.infrastructure.adapter.input.ws.controller.cards;

import com.breixo.culo.domain.port.input.cards.DealCardsUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomEventPublisher;
import com.breixo.culo.infrastructure.adapter.input.ws.api.PostDealingConfirmV1Api;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostDealingConfirmV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.cards.PostDealingConfirmV1RequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.support.game.PlayFollowUpSupport;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * The Class PostDealingConfirmV1Controller.
 */
@Controller
@RequiredArgsConstructor
public class PostDealingConfirmV1Controller implements PostDealingConfirmV1Api {

  /** The deal cards use case. */
  private final DealCardsUseCase dealCardsUseCase;

  /** The post dealing confirm V 1 request mapper. */
  private final PostDealingConfirmV1RequestMapper postDealingConfirmV1RequestMapper;

  /** The room event publisher. */
  private final RoomEventPublisher roomEventPublisher;

  /** The play follow up support. */
  private final PlayFollowUpSupport playFollowUpSupport;

  /** {@inheritDoc} */
  @Override
  @MessageMapping("/dealing.confirm")
  public ResponseEntity<Void> postDealingConfirmV1(
      @Payload @Valid final PostDealingConfirmV1RequestDto postDealingConfirmV1RequestDto) {
 
    final var dealCardsCommand = this.postDealingConfirmV1RequestMapper
        .toDealCardsCommand(postDealingConfirmV1RequestDto);

    final var room = this.dealCardsUseCase.execute(dealCardsCommand);

    this.roomEventPublisher.publishRoomState(room);
    this.playFollowUpSupport.publishPendingQuadDiscards(room);
    this.roomEventPublisher.publishAllHands(room);

    return ResponseEntity.noContent().build();
  }
}
