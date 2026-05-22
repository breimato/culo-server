package com.breixo.culo.infrastructure.adapter.input.ws.controller.swap;

import com.breixo.culo.domain.command.swap.ExchangeGiveCommand;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.swap.ExchangeGiveUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostExchangeGiveV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.swap.PostExchangeGiveV1RequestMapper;
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
 * The Class PostExchangeGiveV1ControllerTest.
 */
@ExtendWith(MockitoExtension.class)
class PostExchangeGiveV1ControllerTest {

  /** The object mapper. */
  final ObjectMapper objectMapper = new ObjectMapper();

  /** The mock mvc. */
  MockMvc mockMvc;

  /** The post exchange give V 1 controller. */
  @InjectMocks
  PostExchangeGiveV1Controller postExchangeGiveV1Controller;

  /** The exchange give use case. */
  @Mock
  ExchangeGiveUseCase exchangeGiveUseCase;

  /** The post exchange give V 1 request mapper. */
  @Mock
  PostExchangeGiveV1RequestMapper postExchangeGiveV1RequestMapper;

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
        this.postExchangeGiveV1Controller, this.wsInboundExceptionSupport, this.objectMapper);
  }

  /**
	 * Test post exchange give V 1 when request is valid then publish exchange
	 * follow up.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostExchangeGiveV1_whenRequestIsValid_thenPublishExchangeFollowUp() throws Exception {
    // Given
    final var postExchangeGiveV1RequestDto = Instancio.create(PostExchangeGiveV1RequestDto.class);
    final var exchangeGiveCommand = Instancio.create(ExchangeGiveCommand.class);
    final var room = Instancio.create(Room.class);

    // When
    when(this.postExchangeGiveV1RequestMapper.toExchangeGiveCommand(postExchangeGiveV1RequestDto))
        .thenReturn(exchangeGiveCommand);
    when(this.exchangeGiveUseCase.execute(exchangeGiveCommand)).thenReturn(room);
    doNothing().when(this.playFollowUpSupport).publishExchangeFollowUp(room);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_EXCHANGE_GIVE_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postExchangeGiveV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postExchangeGiveV1RequestMapper, times(1)).toExchangeGiveCommand(postExchangeGiveV1RequestDto);
    verify(this.exchangeGiveUseCase, times(1)).execute(exchangeGiveCommand);
    verify(this.playFollowUpSupport, times(1)).publishExchangeFollowUp(room);
  }

  /**
	 * Test post exchange give V 1 when use case fails then publish error to client.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostExchangeGiveV1_whenUseCaseFails_thenPublishErrorToClient() throws Exception {
    // Given
    final var postExchangeGiveV1RequestDto = Instancio.create(PostExchangeGiveV1RequestDto.class);
    final var exchangeGiveCommand = Instancio.create(ExchangeGiveCommand.class);
    final var gameException = new GameException(GameExceptionConstants.WRONG_PHASE);

    // When
    when(this.postExchangeGiveV1RequestMapper.toExchangeGiveCommand(postExchangeGiveV1RequestDto))
        .thenReturn(exchangeGiveCommand);
    doThrow(gameException).when(this.exchangeGiveUseCase).execute(exchangeGiveCommand);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_EXCHANGE_GIVE_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postExchangeGiveV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postExchangeGiveV1RequestMapper, times(1)).toExchangeGiveCommand(postExchangeGiveV1RequestDto);
    verify(this.exchangeGiveUseCase, times(1)).execute(exchangeGiveCommand);
    verify(this.wsInboundExceptionSupport, times(1))
        .publishErrorToClient(postExchangeGiveV1RequestDto.getClientId(), gameException);
  }
}
