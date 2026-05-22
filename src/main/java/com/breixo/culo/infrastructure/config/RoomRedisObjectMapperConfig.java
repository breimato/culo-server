package com.breixo.culo.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Class RoomRedisObjectMapperConfig.
 */
@Configuration
public class RoomRedisObjectMapperConfig {

  /**
	 * Room redis object mapper.
	 *
	 * @return the object mapper
	 */
  @Bean("roomRedisObjectMapper")
  ObjectMapper roomRedisObjectMapper() {
    return RoomRedisObjectMapperFactory.create();
  }
}
