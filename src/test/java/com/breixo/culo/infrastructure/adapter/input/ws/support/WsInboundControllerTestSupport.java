package com.breixo.culo.infrastructure.adapter.input.ws.support;

import com.breixo.culo.infrastructure.adapter.input.ws.handler.WsInboundExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * The Class WsInboundControllerTestSupport.
 */
public final class WsInboundControllerTestSupport {

  /**
	 * Instantiates a new ws inbound controller test support.
	 */
  private WsInboundControllerTestSupport() {
  }

  /**
	 * Standalone mock mvc.
	 *
	 * @param controller                the controller
	 * @param wsInboundExceptionSupport the ws inbound exception support
	 * @param objectMapper              the object mapper
	 * @return the mock mvc
	 */
  public static MockMvc standaloneMockMvc(
      final Object controller,
      final WsInboundExceptionSupport wsInboundExceptionSupport,
      final ObjectMapper objectMapper) {
 
    final var wsInboundRequestPayloadSupport = new WsInboundRequestPayloadSupport(objectMapper);
    final var wsInboundExceptionHandler = new WsInboundExceptionHandler(
        wsInboundExceptionSupport, wsInboundRequestPayloadSupport);
    return MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(wsInboundExceptionHandler)
        .build();
  }

  /**
	 * Mock http servlet request.
	 *
	 * @param path           the path
	 * @param requestPayload the request payload
	 * @param objectMapper   the object mapper
	 * @return the mock http servlet request
	 */
  public static MockHttpServletRequest mockHttpServletRequest(
      final String path,
      final Object requestPayload,
      final ObjectMapper objectMapper) {
    try {
      final var mockHttpServletRequest = new MockHttpServletRequest("POST", path);
      mockHttpServletRequest.setContent(objectMapper.writeValueAsBytes(requestPayload));
      mockHttpServletRequest.setContentType(MediaType.APPLICATION_JSON_VALUE);
      return mockHttpServletRequest;
    } catch (final JsonProcessingException jsonProcessingException) {
      throw new IllegalStateException(jsonProcessingException);
    }
  }
}
