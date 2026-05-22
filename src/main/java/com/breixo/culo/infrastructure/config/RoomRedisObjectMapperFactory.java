package com.breixo.culo.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

/**
 * A factory for creating RoomRedisObjectMapper objects.
 */
@UtilityClass
public class RoomRedisObjectMapperFactory {

  /**
	 * Creates the.
	 *
	 * @return the object mapper
	 */
  public static ObjectMapper create() {
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
}
