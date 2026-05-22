package com.breixo.culo.infrastructure.adapter.input.ws.controller.swap;

import com.breixo.culo.domain.command.swap.CuloSwapInitiateCommand;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.swap.CuloSwapInitiateUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomEventPublisher;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostCuloSwapInitiateV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.swap.PostCuloSwapInitiateV1RequestMapper;
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
 * The Class PostCuloSwapInitiateV1ControllerTest.
 */
@ExtendWith(MockitoExtension.class)
class PostCuloSwapInitiateV1ControllerTest {

  /** The object mapper. */
  final ObjectMapper objectMapper = new ObjectMapper();

  /** The mock mvc. */
  MockMvc mockMvc;

  /** The post culo swap initiate V 1 controller. */
  @InjectMocks
  PostCuloSwapInitiateV1Controller postCuloSwapInitiateV1Controller;

  /** The culo swap initiate use case. */
  @Mock
  CuloSwapInitiateUseCase culoSwapInitiateUseCase;

  /** The post culo swap initiate V 1 request mapper. */
  @Mock
  PostCuloSwapInitiateV1RequestMapper postCuloSwapInitiateV1RequestMapper;

  /** The room event publisher. */
  @Mock
  RoomEventPublisher roomEventPublisher;

  /** The ws inbound exception support. */
  @Mock
  WsInboundExceptionSupport wsInboundExceptionSupport;

  /**
	 * Sets the up.
	 */
  @BeforeEach
  void setUp() {
    this.mockMvc = WsInboundControllerTestSupport.standaloneMockMvc(
        this.postCuloSwapInitiateV1Controller, this.wsInboundExceptionSupport, this.objectMapper);
  }

  /**
	 * Test post culo swap initiate V 1 when request is valid then publish culo swap
	 * request.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostCuloSwapInitiateV1_whenRequestIsValid_thenPublishCuloSwapRequest() throws Exception {
    // Given
    final var postCuloSwapInitiateV1RequestDto = Instancio.create(PostCuloSwapInitiateV1RequestDto.class);
    final var culoSwapInitiateCommand = Instancio.create(CuloSwapInitiateCommand.class);
    final var room = Instancio.create(Room.class);

    // When
    when(this.postCuloSwapInitiateV1RequestMapper.toCuloSwapInitiateCommand(postCuloSwapInitiateV1RequestDto))
        .thenReturn(culoSwapInitiateCommand);
    when(this.culoSwapInitiateUseCase.execute(culoSwapInitiateCommand)).thenReturn(room);
    doNothing().when(this.roomEventPublisher).publishRoomState(room);
    doNothing().when(this.roomEventPublisher).publishCuloSwapRequest(room);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_CULO_SWAP_INITIATE_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postCuloSwapInitiateV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postCuloSwapInitiateV1RequestMapper, times(1)).toCuloSwapInitiateCommand(postCuloSwapInitiateV1RequestDto);
    verify(this.culoSwapInitiateUseCase, times(1)).execute(culoSwapInitiateCommand);
    verify(this.roomEventPublisher, times(1)).publishRoomState(room);
    verify(this.roomEventPublisher, times(1)).publishCuloSwapRequest(room);
  }

  /**
	 * Test post culo swap initiate V 1 when use case fails then publish error to client.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostCuloSwapInitiateV1_whenUseCaseFails_thenPublishErrorToClient() throws Exception {
    // Given
    final var postCuloSwapInitiateV1RequestDto = Instancio.create(PostCuloSwapInitiateV1RequestDto.class);
    final var culoSwapInitiateCommand = Instancio.create(CuloSwapInitiateCommand.class);
    final var gameException = new GameException(GameExceptionConstants.WRONG_PHASE);

    // When
    when(this.postCuloSwapInitiateV1RequestMapper.toCuloSwapInitiateCommand(postCuloSwapInitiateV1RequestDto))
        .thenReturn(culoSwapInitiateCommand);
    doThrow(gameException).when(this.culoSwapInitiateUseCase).execute(culoSwapInitiateCommand);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_CULO_SWAP_INITIATE_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postCuloSwapInitiateV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postCuloSwapInitiateV1RequestMapper, times(1)).toCuloSwapInitiateCommand(postCuloSwapInitiateV1RequestDto);
    verify(this.culoSwapInitiateUseCase, times(1)).execute(culoSwapInitiateCommand);
    verify(this.wsInboundExceptionSupport, times(1))
        .publishErrorToClient(postCuloSwapInitiateV1RequestDto.getClientId(), gameException);
  }
}
