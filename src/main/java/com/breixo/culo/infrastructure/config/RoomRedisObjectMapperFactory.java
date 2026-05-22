package com.breixo.culo.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

/** Factory for Jackson ObjectMapper used to serialize rooms in Redis. */
@UtilityClass
public class RoomRedisObjectMapperFactory {

  /**
   * Create object mapper for room redis persistence.
   *
   * @return the object mapper
   */
  public static ObjectMapper create() {
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
}
