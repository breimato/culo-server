package com.breixo.culo.infrastructure.adapter.output.repository.room;

import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.room.RoomMembershipService;
import com.breixo.culo.domain.port.output.room.RoomDeletionPersistencePort;
import com.breixo.culo.domain.port.output.room.RoomExistencePersistencePort;
import com.breixo.culo.domain.port.output.room.RoomRetrievalPersistencePort;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import com.breixo.culo.infrastructure.config.CuloProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Redis-backed room persistence (scenario 1: single instance or sticky sessions). */
@Component
@Profile("redis")
public class RedisRoomStore implements
    RoomSavePersistencePort,
    RoomRetrievalPersistencePort,
    RoomExistencePersistencePort,
    RoomDeletionPersistencePort {

  /** The string redis template. */
  private final StringRedisTemplate stringRedisTemplate;

  /** The room redis object mapper. */
  private final ObjectMapper roomRedisObjectMapper;

  /** The room membership service. */
  private final RoomMembershipService roomMembershipService;

  /** The culo properties. */
  private final CuloProperties culoProperties;

  /**
   * Instantiates a new redis room store.
   *
   * @param stringRedisTemplate    the string redis template
   * @param roomRedisObjectMapper  the room redis object mapper
   * @param roomMembershipService  the room membership service
   * @param culoProperties         the culo properties
   */
  public RedisRoomStore(
      final StringRedisTemplate stringRedisTemplate,
      @Qualifier("roomRedisObjectMapper") final ObjectMapper roomRedisObjectMapper,
      final RoomMembershipService roomMembershipService,
      final CuloProperties culoProperties) {

    this.stringRedisTemplate = stringRedisTemplate;

    this.roomRedisObjectMapper = roomRedisObjectMapper;

    this.roomMembershipService = roomMembershipService;

    this.culoProperties = culoProperties;
  }

  /** {@inheritDoc} */
  @Override
  public Room save(final Room room) {

    final var touchedRoom = this.roomMembershipService.touch(room);

    final var roomCode = touchedRoom.roomLobby().code();

    final var roomKey = RoomRedisKeys.roomKey(roomCode);

    final var ttl = Duration.ofHours(this.culoProperties.getRoom().getTtlHours());

    try {

      final var json = this.roomRedisObjectMapper.writeValueAsString(touchedRoom);

      this.stringRedisTemplate.opsForValue().set(roomKey, json, ttl);

      this.stringRedisTemplate.opsForSet().add(RoomRedisKeys.ROOM_CODES_INDEX, roomCode);

      return touchedRoom;

    } catch (JsonProcessingException jsonProcessingException) {

      throw new IllegalStateException("No se pudo serializar la sala " + roomCode, jsonProcessingException);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Room> findByCode(final String roomCode) {
    final var roomKey = RoomRedisKeys.roomKey(roomCode);
    final var json = this.stringRedisTemplate.opsForValue().get(roomKey);

    if (Objects.isNull(json)) {
      return Optional.empty();
    }

    try {
      final var room = this.roomRedisObjectMapper.readValue(json, Room.class);
      return Optional.of(room);
    } catch (JsonProcessingException jsonProcessingException) {
      throw new IllegalStateException("No se pudo deserializar la sala " + roomCode, jsonProcessingException);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Room> findAll() {
    final var roomCodes = this.stringRedisTemplate.opsForSet().members(RoomRedisKeys.ROOM_CODES_INDEX);

    if (Objects.isNull(roomCodes) || roomCodes.isEmpty()) {
      return List.of();
    }

    final var rooms = new ArrayList<Room>();

    for (final var roomCode : roomCodes) {
      this.findByCode(roomCode).ifPresent(rooms::add);
    }

    return rooms;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByCode(final String roomCode) {
    final var roomKey = RoomRedisKeys.roomKey(roomCode);
    return Boolean.TRUE.equals(this.stringRedisTemplate.hasKey(roomKey));
  }

  /** {@inheritDoc} */
  @Override
  public void deleteByCode(final String roomCode) {
    final var roomKey = RoomRedisKeys.roomKey(roomCode);
    this.stringRedisTemplate.delete(roomKey);
    this.stringRedisTemplate.opsForSet().remove(RoomRedisKeys.ROOM_CODES_INDEX, roomCode);
  }
}
