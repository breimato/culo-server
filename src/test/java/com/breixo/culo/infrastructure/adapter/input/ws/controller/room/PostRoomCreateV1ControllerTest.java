package com.breixo.culo.infrastructure.adapter.input.ws.controller.room;

import com.breixo.culo.domain.command.room.CreateRoomCommand;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.RoomJoinResult;
import com.breixo.culo.domain.port.input.room.CreateRoomUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.RoomEventPublisher;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomCreateV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.PostRoomCreateV1RequestMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.support.WsInboundControllerTestSupport;
import com.breixo.culo.infrastructure.adapter.input.ws.support.WsInboundExceptionSupport;
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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class PostRoomCreateV1ControllerTest.
 */
@ExtendWith(MockitoExtension.class)
class PostRoomCreateV1ControllerTest {

  /** The object mapper. */
  final ObjectMapper objectMapper = new ObjectMapper();

  /** The mock mvc. */
  MockMvc mockMvc;

  /** The post room create V 1 controller. */
  @InjectMocks
  PostRoomCreateV1Controller postRoomCreateV1Controller;

  /** The create room use case. */
  @Mock
  CreateRoomUseCase createRoomUseCase;

  /** The post room create V 1 request mapper. */
  @Mock
  PostRoomCreateV1RequestMapper postRoomCreateV1RequestMapper;

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
        this.postRoomCreateV1Controller, this.wsInboundExceptionSupport, this.objectMapper);
  }

  /**
	 * Test post room create V 1 when request is valid then publish join result.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostRoomCreateV1_whenRequestIsValid_thenPublishJoinResult() throws Exception {
    // Given
    final var postRoomCreateV1RequestDto = Instancio.create(PostRoomCreateV1RequestDto.class);
    final var createRoomCommand = Instancio.create(CreateRoomCommand.class);
    final var roomJoinResult = Instancio.create(RoomJoinResult.class);

    // When
    when(this.postRoomCreateV1RequestMapper.toCreateRoomCommand(postRoomCreateV1RequestDto))
        .thenReturn(createRoomCommand);
    when(this.createRoomUseCase.execute(createRoomCommand)).thenReturn(roomJoinResult);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_ROOM_CREATE_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postRoomCreateV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postRoomCreateV1RequestMapper, times(1)).toCreateRoomCommand(postRoomCreateV1RequestDto);
    verify(this.createRoomUseCase, times(1)).execute(createRoomCommand);
    verify(this.roomEventPublisher, times(1)).publishJoinResult(roomJoinResult);
  }

  /**
	 * Test post room create V 1 when use case fails then publish error to client.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostRoomCreateV1_whenUseCaseFails_thenPublishErrorToClient() throws Exception {
    // Given
    final var postRoomCreateV1RequestDto = Instancio.create(PostRoomCreateV1RequestDto.class);
    final var createRoomCommand = Instancio.create(CreateRoomCommand.class);
    final var roomException = new RoomException(RoomExceptionConstants.ROOM_NOT_FOUND);

    // When
    when(this.postRoomCreateV1RequestMapper.toCreateRoomCommand(postRoomCreateV1RequestDto))
        .thenReturn(createRoomCommand);
    doThrow(roomException).when(this.createRoomUseCase).execute(createRoomCommand);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_ROOM_CREATE_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postRoomCreateV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.wsInboundExceptionSupport, times(1))
        .publishErrorToClient(postRoomCreateV1RequestDto.getClientId(), roomException);
  }
}
