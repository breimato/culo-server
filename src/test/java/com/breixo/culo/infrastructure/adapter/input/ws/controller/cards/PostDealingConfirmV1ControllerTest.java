package com.breixo.culo.infrastructure.adapter.input.ws.controller.cards;

import com.breixo.culo.domain.command.cards.DealCardsCommand;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.cards.DealCardsUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomEventPublisher;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostDealingConfirmV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.cards.PostDealingConfirmV1RequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.support.game.PlayFollowUpSupport;
import com.breixo.culo.infrastructure.adapter.input.ws.support.common.WsInboundControllerTestSupport;
import com.breixo.culo.infrastructure.adapter.input.ws.support.common.WsInboundExceptionSupport;
import com.breixo.culo.infrastructure.config.WsInboundDestinationConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class PostDealingConfirmV1ControllerTest.
 */
@ExtendWith(MockitoExtension.class)
class PostDealingConfirmV1ControllerTest {

  /** The object mapper. */
  final ObjectMapper objectMapper = new ObjectMapper();

  /** The mock mvc. */
  MockMvc mockMvc;

  /** The post dealing confirm V 1 controller. */
  @InjectMocks
  PostDealingConfirmV1Controller postDealingConfirmV1Controller;

  /** The deal cards use case. */
  @Mock
  DealCardsUseCase dealCardsUseCase;

  /** The post dealing confirm V 1 request mapper. */
  @Mock
  PostDealingConfirmV1RequestMapper postDealingConfirmV1RequestMapper;

  /** The room event publisher. */
  @Mock
  RoomEventPublisher roomEventPublisher;

  /** The play follow up support. */
  @Mock
  PlayFollowUpSupport playFollowUpSupport;

  /** The ws inbound exception support. */
  @Mock
  WsInboundExceptionSupport wsInboundExceptionSupport;

  /**
	 * Sets the up.
	 */
  @BeforeEach
  void setUp() {
    this.mockMvc = WsInboundControllerTestSupport.standaloneMockMvc(
        this.postDealingConfirmV1Controller, this.wsInboundExceptionSupport, this.objectMapper);
  }

  /**
	 * Test post dealing confirm V 1 when request is valid then publish room state
	 * and hands.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostDealingConfirmV1_whenRequestIsValid_thenPublishRoomStateAndHands() throws Exception {

    // Given
    final var postDealingConfirmV1RequestDto = Instancio.create(PostDealingConfirmV1RequestDto.class);
    final var dealCardsCommand = Instancio.create(DealCardsCommand.class);
    final var room = Instancio.create(Room.class);

    // When
    when(this.postDealingConfirmV1RequestMapper.toDealCardsCommand(postDealingConfirmV1RequestDto))
        .thenReturn(dealCardsCommand);
    when(this.dealCardsUseCase.execute(dealCardsCommand)).thenReturn(room);
    doNothing().when(this.roomEventPublisher).publishRoomState(room);
    when(this.playFollowUpSupport.publishPendingQuadDiscards(room)).thenReturn(room);
    doNothing().when(this.roomEventPublisher).publishAllHands(room);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_DEALING_CONFIRM_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postDealingConfirmV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postDealingConfirmV1RequestMapper, times(1)).toDealCardsCommand(postDealingConfirmV1RequestDto);
    verify(this.dealCardsUseCase, times(1)).execute(dealCardsCommand);
    verify(this.roomEventPublisher, times(1)).publishRoomState(room);
    verify(this.playFollowUpSupport, times(1)).publishPendingQuadDiscards(room);
    verify(this.roomEventPublisher, times(1)).publishAllHands(room);
  }

  /**
	 * Test post dealing confirm V 1 when use case fails then publish error to client.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostDealingConfirmV1_whenUseCaseFails_thenPublishErrorToClient() throws Exception {
    // Given
    final var postDealingConfirmV1RequestDto = Instancio.create(PostDealingConfirmV1RequestDto.class);
    final var dealCardsCommand = Instancio.create(DealCardsCommand.class);
    final var gameException = new GameException(GameExceptionConstants.NOT_CULO);

    // When
    when(this.postDealingConfirmV1RequestMapper.toDealCardsCommand(postDealingConfirmV1RequestDto))
        .thenReturn(dealCardsCommand);
    doThrow(gameException).when(this.dealCardsUseCase).execute(dealCardsCommand);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_DEALING_CONFIRM_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postDealingConfirmV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postDealingConfirmV1RequestMapper, times(1)).toDealCardsCommand(postDealingConfirmV1RequestDto);
    verify(this.dealCardsUseCase, times(1)).execute(dealCardsCommand);
    verify(this.wsInboundExceptionSupport, times(1))
        .publishErrorToClient(postDealingConfirmV1RequestDto.getClientId(), gameException);
  }
}
