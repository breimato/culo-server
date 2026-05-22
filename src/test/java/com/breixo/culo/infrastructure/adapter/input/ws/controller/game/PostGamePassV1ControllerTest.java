package com.breixo.culo.infrastructure.adapter.input.ws.controller.game;

import com.breixo.culo.domain.command.game.PassCommand;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.game.PassResult;
import com.breixo.culo.domain.port.input.game.PassUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostGamePassV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.game.PostGamePassV1RequestMapper;
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
 * The Class PostGamePassV1ControllerTest.
 */
@ExtendWith(MockitoExtension.class)
class PostGamePassV1ControllerTest {

  /** The object mapper. */
  final ObjectMapper objectMapper = new ObjectMapper();

  /** The mock mvc. */
  MockMvc mockMvc;

  /** The post game pass V 1 controller. */
  @InjectMocks
  PostGamePassV1Controller postGamePassV1Controller;

  /** The pass use case. */
  @Mock
  PassUseCase passUseCase;

  /** The post game pass V 1 request mapper. */
  @Mock
  PostGamePassV1RequestMapper postGamePassV1RequestMapper;

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
        this.postGamePassV1Controller, this.wsInboundExceptionSupport, this.objectMapper);
  }

  /**
	 * Test post game pass V 1 when request is valid then publish pass follow up.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostGamePassV1_whenRequestIsValid_thenPublishPassFollowUp() throws Exception {
    // Given
    final var postGamePassV1RequestDto = Instancio.create(PostGamePassV1RequestDto.class);
    final var passCommand = Instancio.create(PassCommand.class);
    final var passResult = Instancio.create(PassResult.class);
    final var room = passResult.room();

    // When
    when(this.postGamePassV1RequestMapper.toPassCommand(postGamePassV1RequestDto)).thenReturn(passCommand);
    when(this.passUseCase.execute(passCommand)).thenReturn(passResult);
    doNothing().when(this.playFollowUpSupport).publishPassFollowUp(room, passResult.roundClosed());

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_GAME_PASS_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postGamePassV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postGamePassV1RequestMapper, times(1)).toPassCommand(postGamePassV1RequestDto);
    verify(this.passUseCase, times(1)).execute(passCommand);
    verify(this.playFollowUpSupport, times(1)).publishPassFollowUp(room, passResult.roundClosed());
  }

  /**
	 * Test post game pass V 1 when use case fails then publish error to client.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostGamePassV1_whenUseCaseFails_thenPublishErrorToClient() throws Exception {
    // Given
    final var postGamePassV1RequestDto = Instancio.create(PostGamePassV1RequestDto.class);
    final var passCommand = Instancio.create(PassCommand.class);
    final var gameException = new GameException(GameExceptionConstants.NOT_YOUR_TURN);

    // When
    when(this.postGamePassV1RequestMapper.toPassCommand(postGamePassV1RequestDto)).thenReturn(passCommand);
    doThrow(gameException).when(this.passUseCase).execute(passCommand);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_GAME_PASS_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postGamePassV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postGamePassV1RequestMapper, times(1)).toPassCommand(postGamePassV1RequestDto);
    verify(this.passUseCase, times(1)).execute(passCommand);
    verify(this.wsInboundExceptionSupport, times(1))
        .publishErrorToClient(postGamePassV1RequestDto.getClientId(), gameException);
  }
}
