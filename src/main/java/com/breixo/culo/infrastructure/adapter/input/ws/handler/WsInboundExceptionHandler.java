package com.breixo.culo.infrastructure.adapter.input.ws.handler;

import com.breixo.culo.domain.exception.CuloException;
import com.breixo.culo.infrastructure.adapter.input.ws.support.WsInboundExceptionSupport;
import com.breixo.culo.infrastructure.adapter.input.ws.support.WsInboundRequestPayloadSupport;
import com.breixo.culo.infrastructure.config.WsInboundDestinationConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * The Class WsInboundExceptionHandler.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class WsInboundExceptionHandler {

  /** The ws inbound exception support. */
  private final WsInboundExceptionSupport wsInboundExceptionSupport;

  /** The ws inbound request payload support. */
  private final WsInboundRequestPayloadSupport wsInboundRequestPayloadSupport;

  /**
	 * Handle culo exception http.
	 *
	 * @param culoException      the culo exception
	 * @param httpServletRequest the http servlet request
	 * @return the response entity
	 */
  @ExceptionHandler(CuloException.class)
  public ResponseEntity<Void> handleCuloExceptionHttp(
      final CuloException culoException,
      final HttpServletRequest httpServletRequest) {
 
    final var payload = this.wsInboundRequestPayloadSupport.readJsonPayload(httpServletRequest).orElse(null);
    this.publishInboundError(culoException, payload, httpServletRequest.getRequestURI());
    return ResponseEntity.noContent().build();
  }

  /**
	 * Handle culo exception message.
	 *
	 * @param culoException the culo exception
	 * @param message       the message
	 * @return the response entity
	 */
  @MessageExceptionHandler(CuloException.class)
  public ResponseEntity<Void> handleCuloExceptionMessage(
      final CuloException culoException,
      final Message<?> message) {
 
    final var stompHeaderAccessor = StompHeaderAccessor.wrap(message);
    final var destination = stompHeaderAccessor.getDestination();
    this.publishInboundError(culoException, message.getPayload(), destination);
    return ResponseEntity.noContent().build();
  }

  /**
	 * Publish inbound error.
	 *
	 * @param culoException     the culo exception
	 * @param payload           the payload
	 * @param pathOrDestination the path or destination
	 */
  private void publishInboundError(
      final CuloException culoException,
      final Object payload,
      final String pathOrDestination) {
 
    final var clientId = this.wsInboundRequestPayloadSupport.resolveClientId(payload).orElse(null);

    if (this.isRoomStartInbound(pathOrDestination)) {
      final var roomCode = this.wsInboundRequestPayloadSupport.resolveRoomCode(payload).orElse(null);
      this.wsInboundExceptionSupport.publishErrorToClientAndPlayer(clientId, roomCode, culoException);
      return;
    }

    this.wsInboundExceptionSupport.publishErrorToClient(clientId, culoException);
  }

  /**
	 * Checks if is room start inbound.
	 *
	 * @param pathOrDestination the path or destination
	 * @return true, if is room start inbound
	 */
  private boolean isRoomStartInbound(final String pathOrDestination) {
    return StringUtils.contains(pathOrDestination, WsInboundDestinationConstants.POST_ROOM_START_V1);
  }
}
