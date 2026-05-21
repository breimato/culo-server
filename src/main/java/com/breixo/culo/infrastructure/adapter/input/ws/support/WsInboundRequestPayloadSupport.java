package com.breixo.culo.infrastructure.adapter.input.ws.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Objects;
import java.util.Optional;

/**
 * The Class WsInboundRequestPayloadSupport.
 */
@Component
@RequiredArgsConstructor
public class WsInboundRequestPayloadSupport {

  /** The object mapper. */
  private final ObjectMapper objectMapper;

  /**
	 * Resolve client id.
	 *
	 * @param payload the payload
	 * @return the optional
	 */
  public Optional<String> resolveClientId(final Object payload) {
    if (Objects.isNull(payload)) {
      return Optional.empty();
    }
    if (payload instanceof JsonNode jsonNode) {
      return this.resolveStringField(jsonNode, "clientId");
    }
    return this.invokeStringGetter(payload, "getClientId");
  }

  /**
	 * Resolve room code.
	 *
	 * @param payload the payload
	 * @return the optional
	 */
  public Optional<String> resolveRoomCode(final Object payload) {
    if (Objects.isNull(payload)) {
      return Optional.empty();
    }
    if (payload instanceof JsonNode jsonNode) {
      return this.resolveStringField(jsonNode, "roomCode");
    }
    return this.invokeStringGetter(payload, "getRoomCode");
  }

  /**
	 * Read json payload.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the optional
	 */
  public Optional<JsonNode> readJsonPayload(final HttpServletRequest httpServletRequest) {
    if (httpServletRequest instanceof ContentCachingRequestWrapper contentCachingRequestWrapper) {
      return this.readJsonFromBytes(contentCachingRequestWrapper.getContentAsByteArray());
    }
    return this.readJsonFromBytes(this.extractContentBytes(httpServletRequest));
  }

  /**
	 * Extract content bytes.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the byte[]
	 */
  private byte[] extractContentBytes(final HttpServletRequest httpServletRequest) {
    try {
      final var method = httpServletRequest.getClass().getMethod("getContentAsByteArray");
      return (byte[]) method.invoke(httpServletRequest);
    } catch (final ReflectiveOperationException reflectiveOperationException) {
      return new byte[0];
    }
  }

  /**
	 * Read json from bytes.
	 *
	 * @param content the content
	 * @return the optional
	 */
  private Optional<JsonNode> readJsonFromBytes(final byte[] content) {
    if (Objects.isNull(content) || Integer.valueOf(0).equals(content.length)) {
      return Optional.empty();
    }
    try {
      return Optional.of(this.objectMapper.readTree(content));
    } catch (final Exception exception) {
      return Optional.empty();
    }
  }

  /**
	 * Resolve string field.
	 *
	 * @param jsonNode  the json node
	 * @param fieldName the field name
	 * @return the optional
	 */
  private Optional<String> resolveStringField(final JsonNode jsonNode, final String fieldName) {
    if (jsonNode.has(fieldName) && StringUtils.isNotBlank(jsonNode.get(fieldName).asText())) {
      return Optional.of(jsonNode.get(fieldName).asText());
    }
    return Optional.empty();
  }

  /**
	 * Invoke string getter.
	 *
	 * @param payload    the payload
	 * @param getterName the getter name
	 * @return the optional
	 */
  private Optional<String> invokeStringGetter(final Object payload, final String getterName) {
    try {
      final var method = payload.getClass().getMethod(getterName);
      final var value = method.invoke(payload);
      if (value instanceof String stringValue && StringUtils.isNotBlank(stringValue)) {
        return Optional.of(stringValue);
      }
      return Optional.empty();
    } catch (final ReflectiveOperationException reflectiveOperationException) {
      return Optional.empty();
    }
  }
}
