package com.breixo.culo.infrastructure.adapter.input.ws.controller.swap;

import com.breixo.culo.domain.command.swap.CuloSwapVoteCommand;
import com.breixo.culo.domain.model.swap.CuloSwapVoteResponse;
import com.breixo.culo.domain.port.input.swap.CuloSwapVoteUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostCuloSwapVoteV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.swap.PostCuloSwapVoteV1RequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.support.swap.CuloSwapVoteFollowUpSupport;
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
 * The Class PostCuloSwapVoteV1ControllerTest.
 */
@ExtendWith(MockitoExtension.class)
class PostCuloSwapVoteV1ControllerTest {

  /** The object mapper. */
  final ObjectMapper objectMapper = new ObjectMapper();

  /** The mock mvc. */
  MockMvc mockMvc;

  /** The post culo swap vote V 1 controller. */
  @InjectMocks
  PostCuloSwapVoteV1Controller postCuloSwapVoteV1Controller;

  /** The culo swap vote use case. */
  @Mock
  CuloSwapVoteUseCase culoSwapVoteUseCase;

  /** The post culo swap vote V 1 request mapper. */
  @Mock
  PostCuloSwapVoteV1RequestMapper postCuloSwapVoteV1RequestMapper;

  /** The culo swap vote follow up support. */
  @Mock
  CuloSwapVoteFollowUpSupport culoSwapVoteFollowUpSupport;

  /** The ws inbound exception support. */
  @Mock
  WsInboundExceptionSupport wsInboundExceptionSupport;

  /**
	 * Sets the up.
	 */
  @BeforeEach
  void setUp() {
    this.mockMvc = WsInboundControllerTestSupport.standaloneMockMvc(
        this.postCuloSwapVoteV1Controller, this.wsInboundExceptionSupport, this.objectMapper);
  }

  /**
	 * Test post culo swap vote V 1 when request is valid then publish vote follow
	 * up.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostCuloSwapVoteV1_whenRequestIsValid_thenPublishVoteFollowUp() throws Exception {
    // Given
    final var postCuloSwapVoteV1RequestDto = Instancio.create(PostCuloSwapVoteV1RequestDto.class);
    final var culoSwapVoteCommand = Instancio.create(CuloSwapVoteCommand.class);
    final var culoVoteResult = Instancio.create(CuloSwapVoteResponse.class);

    // When
    when(this.postCuloSwapVoteV1RequestMapper.toCuloSwapVoteCommand(postCuloSwapVoteV1RequestDto))
        .thenReturn(culoSwapVoteCommand);
    when(this.culoSwapVoteUseCase.execute(culoSwapVoteCommand)).thenReturn(culoVoteResult);
    doNothing().when(this.culoSwapVoteFollowUpSupport).publishVoteFollowUp(culoVoteResult);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_CULO_SWAP_VOTE_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postCuloSwapVoteV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postCuloSwapVoteV1RequestMapper, times(1)).toCuloSwapVoteCommand(postCuloSwapVoteV1RequestDto);
    verify(this.culoSwapVoteUseCase, times(1)).execute(culoSwapVoteCommand);
    verify(this.culoSwapVoteFollowUpSupport, times(1)).publishVoteFollowUp(culoVoteResult);
  }

  /**
	 * Test post culo swap vote V 1 when use case fails then publish error to
	 * client.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostCuloSwapVoteV1_whenUseCaseFails_thenPublishErrorToClient() throws Exception {
    // Given
    final var postCuloSwapVoteV1RequestDto = Instancio.create(PostCuloSwapVoteV1RequestDto.class);
    final var culoSwapVoteCommand = Instancio.create(CuloSwapVoteCommand.class);
    final var gameException = new GameException(GameExceptionConstants.WRONG_PHASE);

    // When
    when(this.postCuloSwapVoteV1RequestMapper.toCuloSwapVoteCommand(postCuloSwapVoteV1RequestDto))
        .thenReturn(culoSwapVoteCommand);
    doThrow(gameException).when(this.culoSwapVoteUseCase).execute(culoSwapVoteCommand);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_CULO_SWAP_VOTE_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postCuloSwapVoteV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postCuloSwapVoteV1RequestMapper, times(1)).toCuloSwapVoteCommand(postCuloSwapVoteV1RequestDto);
    verify(this.culoSwapVoteUseCase, times(1)).execute(culoSwapVoteCommand);
    verify(this.wsInboundExceptionSupport, times(1))
        .publishErrorToClient(postCuloSwapVoteV1RequestDto.getClientId(), gameException);
  }
}
