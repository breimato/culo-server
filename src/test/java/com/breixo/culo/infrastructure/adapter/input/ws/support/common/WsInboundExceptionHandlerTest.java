package com.breixo.culo.infrastructure.adapter.input.ws.support.common;

import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomCreateV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomStartV1RequestDto;
import com.breixo.culo.infrastructure.adapter.input.ws.support.common.WsInboundControllerTestSupport;
import com.breixo.culo.infrastructure.adapter.input.ws.support.common.WsInboundExceptionSupport;
import com.breixo.culo.infrastructure.adapter.input.ws.support.common.WsInboundRequestPayloadSupport;
import com.breixo.culo.infrastructure.config.WsInboundDestinationConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The Class WsInboundExceptionHandlerTest.
 */
@ExtendWith(MockitoExtension.class)
class WsInboundExceptionHandlerTest {

  /** The object mapper. */
  final ObjectMapper objectMapper = new ObjectMapper();

  /** The ws inbound exception support. */
  @Mock
  WsInboundExceptionSupport wsInboundExceptionSupport;

  /** The ws inbound request payload support. */
  WsInboundRequestPayloadSupport wsInboundRequestPayloadSupport;

  /** The ws inbound exception handler. */
  WsInboundExceptionHandler wsInboundExceptionHandler;

  /**
	 * Sets the up.
	 */
  @BeforeEach
  void setUp() {
    this.wsInboundRequestPayloadSupport = new WsInboundRequestPayloadSupport(this.objectMapper);
    this.wsInboundExceptionHandler = new WsInboundExceptionHandler(
        this.wsInboundExceptionSupport, this.wsInboundRequestPayloadSupport);
  }

  /**
	 * Test handle culo exception http when room start then publish error to client
	 * and player.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testHandleCuloExceptionHttp_whenRoomStart_thenPublishErrorToClientAndPlayer() throws Exception {

    // Given
    final var postRoomStartV1RequestDto = Instancio.create(PostRoomStartV1RequestDto.class);
    final var roomException = new RoomException(RoomExceptionConstants.NOT_HOST);
    final var mockHttpServletRequest = WsInboundControllerTestSupport.mockHttpServletRequest(
        WsInboundDestinationConstants.POST_ROOM_START_V1,
        postRoomStartV1RequestDto,
        this.objectMapper);

    // When
    final var responseEntity = this.wsInboundExceptionHandler.handleCuloExceptionHttp(
        roomException, mockHttpServletRequest);

    // Then
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(this.wsInboundExceptionSupport, times(1)).publishErrorToClientAndPlayer(
        postRoomStartV1RequestDto.getClientId(),
        postRoomStartV1RequestDto.getRoomCode(),
        roomException);
  }

  /**
	 * Test handle culo exception http when room create then publish error to
	 * client.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testHandleCuloExceptionHttp_whenRoomCreate_thenPublishErrorToClient() throws Exception {

    // Given
    final var postRoomCreateV1RequestDto = Instancio.create(PostRoomCreateV1RequestDto.class);
    final var roomException = new RoomException(RoomExceptionConstants.ROOM_NOT_FOUND);
    final var mockHttpServletRequest = WsInboundControllerTestSupport.mockHttpServletRequest(
        WsInboundDestinationConstants.POST_ROOM_CREATE_V1,
        postRoomCreateV1RequestDto,
        this.objectMapper);

    // When
    final var responseEntity = this.wsInboundExceptionHandler.handleCuloExceptionHttp(
        roomException, mockHttpServletRequest);

    // Then
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(this.wsInboundExceptionSupport, times(1)).publishErrorToClient(
        postRoomCreateV1RequestDto.getClientId(), roomException);
  }
}
