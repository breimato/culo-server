package com.breixo.culo.infrastructure.adapter.output.repository.room;

import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.infrastructure.config.RoomRedisObjectMapperFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The Class RoomRedisSerializationTest.
 */
class RoomRedisSerializationTest {

  /** The room redis object mapper. */
  private final ObjectMapper roomRedisObjectMapper = RoomRedisObjectMapperFactory.create();

  /**
	 * Test round trip when room from instancio then equals after json.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testRoundTrip_whenRoomFromInstancio_thenEqualsAfterJson() throws Exception {
    // Given
    final var room = Instancio.create(Room.class);

    // When
    final var json = this.roomRedisObjectMapper.writeValueAsString(room);
    final var restoredRoom = this.roomRedisObjectMapper.readValue(json, Room.class);

    // Then
    assertEquals(room, restoredRoom);
  }
}
