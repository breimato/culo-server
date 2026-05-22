package com.breixo.culo.infrastructure.adapter.output.repository.room;

import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import com.breixo.culo.domain.service.room.RoomMembershipServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Class MemoryRoomStoreTest.
 */
class MemoryRoomStoreTest {

  /** The memory room store. */
  MemoryRoomStore memoryRoomStore;

  /**
	 * Inits the.
	 */
  @BeforeEach
  void init() {
    final var roomMembershipService = new RoomMembershipServiceImpl();
    this.memoryRoomStore = new MemoryRoomStore(roomMembershipService);
  }

  /**
	 * Test save when room provided then store and return touched room.
	 */
  @Test
  void testSave_whenRoomProvided_thenStoreAndReturnTouchedRoom() {
    // Given
    final var lastActivity = Instant.parse("2020-01-01T00:00:00Z");
    final var roomLobby = Instancio.of(RoomLobby.class)
        .set(field(RoomLobby::code), "ABCD")
        .set(field(RoomLobby::lastActivity), lastActivity)
        .create();
    final var room = Instancio.of(Room.class)
        .set(field(Room::roomLobby), roomLobby)
        .create();

    // When
    final var savedRoom = this.memoryRoomStore.save(room);

    // Then
    assertTrue(savedRoom.roomLobby().lastActivity().isAfter(lastActivity));
    final var foundRoom = this.memoryRoomStore.findByCode("ABCD").orElseThrow();
    assertEquals(savedRoom, foundRoom);
  }

  /**
	 * Test find by code when room missing then empty.
	 */
  @Test
  void testFindByCode_whenRoomMissing_thenEmpty() {
    // When
    final var roomOptional = this.memoryRoomStore.findByCode("WXYZ");

    // Then
    assertTrue(roomOptional.isEmpty());
  }

  /**
	 * Test find all when rooms saved then return all values.
	 */
  @Test
  void testFindAll_whenRoomsSaved_thenReturnAllValues() {
    // Given
    final var roomLobbyOne = Instancio.of(RoomLobby.class)
        .set(field(RoomLobby::code), "ROOM")
        .create();
    final var roomOne = Instancio.of(Room.class)
        .set(field(Room::roomLobby), roomLobbyOne)
        .create();
    final var roomLobbyTwo = Instancio.of(RoomLobby.class)
        .set(field(RoomLobby::code), "GAME")
        .create();
    final var roomTwo = Instancio.of(Room.class)
        .set(field(Room::roomLobby), roomLobbyTwo)
        .create();
    this.memoryRoomStore.save(roomOne);
    this.memoryRoomStore.save(roomTwo);

    // When
    final var rooms = this.memoryRoomStore.findAll();

    // Then
    assertEquals(2, rooms.size());
  }

  /**
	 * Test exists by code when room present then true.
	 */
  @Test
  void testExistsByCode_whenRoomPresent_thenTrue() {
    // Given
    final var roomLobby = Instancio.of(RoomLobby.class)
        .set(field(RoomLobby::code), "EXIST")
        .create();
    final var room = Instancio.of(Room.class)
        .set(field(Room::roomLobby), roomLobby)
        .create();
    this.memoryRoomStore.save(room);

    // When
    final var exists = this.memoryRoomStore.existsByCode("EXIST");

    // Then
    assertTrue(exists);
  }

  /**
	 * Test exists by code when room missing then false.
	 */
  @Test
  void testExistsByCode_whenRoomMissing_thenFalse() {
    // When
    final var exists = this.memoryRoomStore.existsByCode("NONE");

    // Then
    assertFalse(exists);
  }

  /**
	 * Test delete by code when room present then remove.
	 */
  @Test
  void testDeleteByCode_whenRoomPresent_thenRemove() {
    // Given
    final var roomLobby = Instancio.of(RoomLobby.class)
        .set(field(RoomLobby::code), "DEL1")
        .create();
    final var room = Instancio.of(Room.class)
        .set(field(Room::roomLobby), roomLobby)
        .create();
    this.memoryRoomStore.save(room);

    // When
    this.memoryRoomStore.deleteByCode("DEL1");

    // Then
    assertTrue(this.memoryRoomStore.findByCode("DEL1").isEmpty());
    assertFalse(this.memoryRoomStore.existsByCode("DEL1"));
  }
}
