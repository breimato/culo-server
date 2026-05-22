package com.breixo.culo.infrastructure.config;

import com.breixo.culo.domain.model.room.Room;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * The Class RoomRedisObjectMapper.
 */
@Component
@Profile("redis")
public class RoomRedisObjectMapper {

  /** The object mapper. */
  private final ObjectMapper objectMapper;

  /**
	 * Instantiates a new room redis object mapper.
	 */
  public RoomRedisObjectMapper() {
    this.objectMapper = RoomRedisObjectMapperFactory.create();
  }

  /**
	 * To json.
	 *
	 * @param room the room
	 * @return the string
	 * @throws JsonProcessingException the json processing exception
	 */
  public String toJson(final Room room) throws JsonProcessingException {
    return this.objectMapper.writeValueAsString(room);
  }

  /**
	 * From json.
	 *
	 * @param json the json
	 * @return the room
	 * @throws JsonProcessingException the json processing exception
	 */
  public Room fromJson(final String json) throws JsonProcessingException {
    return this.objectMapper.readValue(json, Room.class);
  }
}
