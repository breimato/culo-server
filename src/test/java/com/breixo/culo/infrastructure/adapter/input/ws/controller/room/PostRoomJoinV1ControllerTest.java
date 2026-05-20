package com.breixo.culo.infrastructure.adapter.input.ws.controller.room;

import com.breixo.culo.domain.command.room.JoinRoomCommand;
import com.breixo.culo.domain.model.room.RoomJoinResult;
import com.breixo.culo.domain.port.input.room.JoinRoomUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.RoomEventPublisher;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomJoinV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.PostRoomJoinV1RequestMapper;
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

import static org.instancio.Select.field;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The Class PostRoomJoinV1ControllerTest.
 */
@ExtendWith(MockitoExtension.class)
class PostRoomJoinV1ControllerTest {

  /** The object mapper. */
  final ObjectMapper objectMapper = new ObjectMapper();

  /** The mock mvc. */
  MockMvc mockMvc;

  /** The post room join V 1 controller. */
  @InjectMocks
  PostRoomJoinV1Controller postRoomJoinV1Controller;

  /** The join room use case. */
  @Mock
  JoinRoomUseCase joinRoomUseCase;

  /** The post room join V 1 request mapper. */
  @Mock
  PostRoomJoinV1RequestMapper postRoomJoinV1RequestMapper;

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
        this.postRoomJoinV1Controller, this.wsInboundExceptionSupport, this.objectMapper);
  }

  /**
	 * Test post room join V 1 when request is valid then publish join result.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostRoomJoinV1_whenRequestIsValid_thenPublishJoinResult() throws Exception {
    // Given
    final var postRoomJoinV1RequestDto = Instancio.of(PostRoomJoinV1RequestDto.class)
        .generate(field(PostRoomJoinV1RequestDto::getRoomCode), gen -> gen.string().length(4))
        .generate(field(PostRoomJoinV1RequestDto::getNick), gen -> gen.string().length(1, 20))
        .create();
    final var joinRoomCommand = Instancio.create(JoinRoomCommand.class);
    final var roomJoinResult = Instancio.create(RoomJoinResult.class);

    // When
    when(this.postRoomJoinV1RequestMapper.toJoinRoomCommand(postRoomJoinV1RequestDto)).thenReturn(joinRoomCommand);
    when(this.joinRoomUseCase.execute(joinRoomCommand)).thenReturn(roomJoinResult);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_ROOM_JOIN_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postRoomJoinV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postRoomJoinV1RequestMapper, times(1)).toJoinRoomCommand(postRoomJoinV1RequestDto);
    verify(this.joinRoomUseCase, times(1)).execute(joinRoomCommand);
    verify(this.roomEventPublisher, times(1)).publishJoinResult(roomJoinResult);
  }
}
