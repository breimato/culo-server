package com.breixo.culo.infrastructure.adapter.output.repository.room;

import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import com.breixo.culo.domain.port.input.room.RoomMembershipService;
import com.breixo.culo.infrastructure.config.CuloProperties;
import com.breixo.culo.infrastructure.config.RoomRedisObjectMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class RedisRoomStoreTest.
 */
@ExtendWith(MockitoExtension.class)
class RedisRoomStoreTest {

  /** The Constant ROOM_CODE. */
  private static final String ROOM_CODE = "ABCD";

  /** The string redis template. */
  @Mock
  StringRedisTemplate stringRedisTemplate;

  /** The value operations. */
  @Mock
  ValueOperations<String, String> valueOperations;

  /** The set operations. */
  @Mock
  SetOperations<String, String> setOperations;

  /** The room membership service. */
  @Mock
  RoomMembershipService roomMembershipService;

  /** The culo properties. */
  @Mock
  CuloProperties culoProperties;

  /** The room redis object mapper. */
  ObjectMapper roomRedisObjectMapper;

  /** The redis room store. */
  RedisRoomStore redisRoomStore;

  /**
	 * Inits the.
	 */
  @BeforeEach
  void init() {
    this.roomRedisObjectMapper = RoomRedisObjectMapperFactory.create();
    this.redisRoomStore = new RedisRoomStore(
        this.stringRedisTemplate,
        this.roomRedisObjectMapper,
        this.roomMembershipService,
        this.culoProperties);
    lenient().when(this.stringRedisTemplate.opsForValue()).thenReturn(this.valueOperations);
    lenient().when(this.stringRedisTemplate.opsForSet()).thenReturn(this.setOperations);
  }

  /**
	 * Test save when room valid then touch set key and add to index.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testSave_whenRoomValid_thenTouchSetKeyAndAddToIndex() throws Exception {
    // Given
    final var roomLobby = Instancio.of(RoomLobby.class)
        .set(field(RoomLobby::code), ROOM_CODE)
        .create();
    final var room = Instancio.of(Room.class)
        .set(field(Room::roomLobby), roomLobby)
        .create();
    final var touchedRoom = Instancio.of(Room.class)
        .set(field(Room::roomLobby), roomLobby)
        .create();
    when(this.roomMembershipService.touch(room)).thenReturn(touchedRoom);
    final var roomProperties = CuloProperties.Room.builder().ttlHours(2).build();
    when(this.culoProperties.getRoom()).thenReturn(roomProperties);
    final var json = this.roomRedisObjectMapper.writeValueAsString(touchedRoom);
    final var roomKey = RoomRedisKeys.roomKey(ROOM_CODE);
    final var ttl = Duration.ofHours(2);

    // When
    final var savedRoom = this.redisRoomStore.save(room);

    // Then
    verify(this.roomMembershipService, times(1)).touch(room);
    verify(this.valueOperations, times(1)).set(roomKey, json, ttl);
    verify(this.setOperations, times(1)).add(RoomRedisKeys.ROOM_CODES_INDEX, ROOM_CODE);
    assertEquals(touchedRoom, savedRoom);
  }

  /**
	 * Test find by code when key exists then return room.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testFindByCode_whenKeyExists_thenReturnRoom() throws Exception {
    // Given
    final var room = Instancio.create(Room.class);
    final var roomCode = room.roomLobby().code();
    final var roomKey = RoomRedisKeys.roomKey(roomCode);
    final var json = this.roomRedisObjectMapper.writeValueAsString(room);
    when(this.valueOperations.get(roomKey)).thenReturn(json);

    // When
    final var roomOptional = this.redisRoomStore.findByCode(roomCode);

    // Then
    verify(this.valueOperations, times(1)).get(roomKey);
    assertTrue(roomOptional.isPresent());
    assertEquals(room, roomOptional.get());
  }

  /**
	 * Test find by code when key missing then empty.
	 */
  @Test
  void testFindByCode_whenKeyMissing_thenEmpty() {
    // Given
    final var roomKey = RoomRedisKeys.roomKey(ROOM_CODE);
    when(this.valueOperations.get(roomKey)).thenReturn(null);

    // When
    final var roomOptional = this.redisRoomStore.findByCode(ROOM_CODE);

    // Then
    verify(this.valueOperations, times(1)).get(roomKey);
    assertTrue(roomOptional.isEmpty());
  }

  /**
	 * Test exists by code when key present then true.
	 */
  @Test
  void testExistsByCode_whenKeyPresent_thenTrue() {
    // Given
    final var roomKey = RoomRedisKeys.roomKey(ROOM_CODE);
    when(this.stringRedisTemplate.hasKey(roomKey)).thenReturn(Boolean.TRUE);

    // When
    final var exists = this.redisRoomStore.existsByCode(ROOM_CODE);

    // Then
    verify(this.stringRedisTemplate, times(1)).hasKey(roomKey);
    assertTrue(exists);
  }

  /**
	 * Test exists by code when key missing then false.
	 */
  @Test
  void testExistsByCode_whenKeyMissing_thenFalse() {
    // Given
    final var roomKey = RoomRedisKeys.roomKey(ROOM_CODE);
    when(this.stringRedisTemplate.hasKey(roomKey)).thenReturn(Boolean.FALSE);

    // When
    final var exists = this.redisRoomStore.existsByCode(ROOM_CODE);

    // Then
    verify(this.stringRedisTemplate, times(1)).hasKey(roomKey);
    assertFalse(exists);
  }

  /**
	 * Test delete by code when called then del key and srem index.
	 */
  @Test
  void testDeleteByCode_whenCalled_thenDelKeyAndSremIndex() {
    // Given
    final var roomKey = RoomRedisKeys.roomKey(ROOM_CODE);

    // When
    this.redisRoomStore.deleteByCode(ROOM_CODE);

    // Then
    verify(this.stringRedisTemplate, times(1)).delete(roomKey);
    verify(this.setOperations, times(1)).remove(RoomRedisKeys.ROOM_CODES_INDEX, ROOM_CODE);
  }

  /**
	 * Test find all when index has codes then return only existing rooms.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testFindAll_whenIndexHasCodes_thenReturnOnlyExistingRooms() throws Exception {
    // Given
    final var room = Instancio.create(Room.class);
    final var roomCode = room.roomLobby().code();
    final var expiredRoomCode = "EXPI";
    final var roomCodes = new LinkedHashSet<String>();
    roomCodes.add(roomCode);
    roomCodes.add(expiredRoomCode);
    when(this.setOperations.members(RoomRedisKeys.ROOM_CODES_INDEX)).thenReturn(roomCodes);
    final var roomKey = RoomRedisKeys.roomKey(roomCode);
    final var json = this.roomRedisObjectMapper.writeValueAsString(room);
    when(this.valueOperations.get(roomKey)).thenReturn(json);
    final var expiredRoomKey = RoomRedisKeys.roomKey(expiredRoomCode);
    when(this.valueOperations.get(expiredRoomKey)).thenReturn(null);

    // When
    final var rooms = this.redisRoomStore.findAll();

    // Then
    assertEquals(1, rooms.size());
    assertEquals(room, rooms.iterator().next());
  }

  /**
	 * Test find all when index empty then return empty list.
	 */
  @Test
  void testFindAll_whenIndexEmpty_thenReturnEmptyList() {
    // Given
    when(this.setOperations.members(RoomRedisKeys.ROOM_CODES_INDEX)).thenReturn(Set.of());

    // When
    final var rooms = this.redisRoomStore.findAll();

    // Then
    assertTrue(rooms.isEmpty());
  }

  /**
	 * Test save when mapper fails to write then throw illegal state exception.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testSave_whenMapperFailsToWrite_thenThrowIllegalStateException() throws Exception {
    // Given
    final var roomLobby = Instancio.of(RoomLobby.class)
        .set(field(RoomLobby::code), ROOM_CODE)
        .create();
    final var room = Instancio.of(Room.class)
        .set(field(Room::roomLobby), roomLobby)
        .create();
    final var touchedRoom = Instancio.of(Room.class)
        .set(field(Room::roomLobby), roomLobby)
        .create();
    when(this.roomMembershipService.touch(room)).thenReturn(touchedRoom);
    final var roomProperties = CuloProperties.Room.builder().ttlHours(2).build();
    when(this.culoProperties.getRoom()).thenReturn(roomProperties);
    final var failingObjectMapper = mock(ObjectMapper.class);
    final var jsonProcessingException = new JsonProcessingException("fail") {
    };
    doThrow(jsonProcessingException).when(failingObjectMapper).writeValueAsString(touchedRoom);
    final var redisRoomStoreWithFailingMapper = new RedisRoomStore(
        this.stringRedisTemplate,
        failingObjectMapper,
        this.roomMembershipService,
        this.culoProperties);

    // When
    final var illegalStateException = assertThrows(
        IllegalStateException.class,
        () -> redisRoomStoreWithFailingMapper.save(room));

    // Then
    verify(this.roomMembershipService, times(1)).touch(room);
    assertTrue(illegalStateException.getMessage().contains(ROOM_CODE));
  }

  /**
	 * Test find by code when mapper fails to read then throw illegal state
	 * exception.
	 *
	 * @throws Exception the exception
	 */
  @Test
  void testFindByCode_whenMapperFailsToRead_thenThrowIllegalStateException() throws Exception {
    // Given
    final var roomKey = RoomRedisKeys.roomKey(ROOM_CODE);
    when(this.valueOperations.get(roomKey)).thenReturn("not-valid-json");
    final var failingObjectMapper = mock(ObjectMapper.class);
    final var jsonProcessingException = new JsonProcessingException("fail") {
    };
    when(failingObjectMapper.readValue("not-valid-json", Room.class)).thenThrow(jsonProcessingException);
    final var redisRoomStoreWithFailingMapper = new RedisRoomStore(
        this.stringRedisTemplate,
        failingObjectMapper,
        this.roomMembershipService,
        this.culoProperties);

    // When
    final var illegalStateException = assertThrows(
        IllegalStateException.class,
        () -> redisRoomStoreWithFailingMapper.findByCode(ROOM_CODE));

    // Then
    verify(this.valueOperations, times(1)).get(roomKey);
    assertTrue(illegalStateException.getMessage().contains(ROOM_CODE));
  }
}
