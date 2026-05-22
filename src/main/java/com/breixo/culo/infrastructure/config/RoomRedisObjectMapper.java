package com.breixo.culo.infrastructure.config;

import com.breixo.culo.domain.model.room.Room;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Jackson wrapper for serializing {@link Room} in Redis. */
@Component
@Profile("redis")
public class RoomRedisObjectMapper {

  /** The object mapper. */
  private final ObjectMapper objectMapper;

  /** Instantiates a new room redis object mapper. */
  public RoomRedisObjectMapper() {
    this.objectMapper = RoomRedisObjectMapperFactory.create();
  }

  /**
   * Serialize room to json.
   *
   * @param room the room
   * @return the json
   * @throws JsonProcessingException the json processing exception
   */
  public String toJson(final Room room) throws JsonProcessingException {
    return this.objectMapper.writeValueAsString(room);
  }

  /**
   * Deserialize room from json.
   *
   * @param json the json
   * @return the room
   * @throws JsonProcessingException the json processing exception
   */
  public Room fromJson(final String json) throws JsonProcessingException {
    return this.objectMapper.readValue(json, Room.class);
  }
}
