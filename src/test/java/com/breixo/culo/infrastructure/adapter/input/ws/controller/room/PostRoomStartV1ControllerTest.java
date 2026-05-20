package com.breixo.culo.infrastructure.adapter.input.ws.controller.room;

import com.breixo.culo.domain.command.room.StartGameCommand;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.port.input.room.StartGameUseCase;
import com.breixo.culo.infrastructure.adapter.input.ws.RoomEventPublisher;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomStartV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.PostRoomStartV1RequestMapper;
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
 * The Class PostRoomStartV1ControllerTest.
 */
@ExtendWith(MockitoExtension.class)
class PostRoomStartV1ControllerTest {

  /** The object mapper. */
  final ObjectMapper objectMapper = new ObjectMapper();

  /** The mock mvc. */
  MockMvc mockMvc;

  /** The post room start V 1 controller. */
  @InjectMocks
  PostRoomStartV1Controller postRoomStartV1Controller;

  /** The start game use case. */
  @Mock
  StartGameUseCase startGameUseCase;

  /** The post room start V 1 request mapper. */
  @Mock
  PostRoomStartV1RequestMapper postRoomStartV1RequestMapper;

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
        this.postRoomStartV1Controller, this.wsInboundExceptionSupport, this.objectMapper);
  }

  /**
	 * Test post room start V 1 when request is valid then publish room state.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostRoomStartV1_whenRequestIsValid_thenPublishRoomState() throws Exception {
    // Given
    final var postRoomStartV1RequestDto = Instancio.create(PostRoomStartV1RequestDto.class);
    final var startGameCommand = Instancio.create(StartGameCommand.class);
    final var room = Instancio.create(Room.class);

    // When
    when(this.postRoomStartV1RequestMapper.toStartGameCommand(postRoomStartV1RequestDto)).thenReturn(startGameCommand);
    when(this.startGameUseCase.execute(startGameCommand)).thenReturn(room);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_ROOM_START_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postRoomStartV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.postRoomStartV1RequestMapper, times(1)).toStartGameCommand(postRoomStartV1RequestDto);
    verify(this.startGameUseCase, times(1)).execute(startGameCommand);
    verify(this.roomEventPublisher, times(1)).publishRoomState(room);
  }

  /**
	 * Test post room start V 1 when use case fails then publish error to client and
	 * player.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testPostRoomStartV1_whenUseCaseFails_thenPublishErrorToClientAndPlayer() throws Exception {
    // Given
    final var postRoomStartV1RequestDto = Instancio.create(PostRoomStartV1RequestDto.class);
    final var startGameCommand = Instancio.create(StartGameCommand.class);
    final var roomException = new RoomException(RoomExceptionConstants.NOT_HOST);

    // When
    when(this.postRoomStartV1RequestMapper.toStartGameCommand(postRoomStartV1RequestDto)).thenReturn(startGameCommand);
    doThrow(roomException).when(this.startGameUseCase).execute(startGameCommand);

    this.mockMvc.perform(post(WsInboundDestinationConstants.POST_ROOM_START_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(postRoomStartV1RequestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(this.wsInboundExceptionSupport, times(1)).publishErrorToClientAndPlayer(
        postRoomStartV1RequestDto.getClientId(),
        postRoomStartV1RequestDto.getRoomCode(),
        roomException);
  }
}
