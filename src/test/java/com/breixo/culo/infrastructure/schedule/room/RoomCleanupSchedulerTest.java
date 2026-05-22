package com.breixo.culo.infrastructure.schedule.room;

import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import com.breixo.culo.domain.port.output.room.RoomDeletionPersistencePort;
import com.breixo.culo.domain.port.output.room.RoomRetrievalPersistencePort;
import com.breixo.culo.infrastructure.config.CuloProperties;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.instancio.Select.field;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class RoomCleanupSchedulerTest.
 */
@ExtendWith(MockitoExtension.class)
class RoomCleanupSchedulerTest {

  /** The room cleanup scheduler. */
  @InjectMocks
  RoomCleanupScheduler roomCleanupScheduler;

  /** The room retrieval persistence port. */
  @Mock
  RoomRetrievalPersistencePort roomRetrievalPersistencePort;

  /** The room deletion persistence port. */
  @Mock
  RoomDeletionPersistencePort roomDeletionPersistencePort;

  /** The culo properties. */
  @Mock
  CuloProperties culoProperties;

  /**
	 * Test purge inactive rooms when room expired then delete by code.
	 */
  @Test
  void testPurgeInactiveRooms_whenRoomExpired_thenDeleteByCode() {
    // Given
    final var roomProperties = CuloProperties.Room.builder().ttlHours(2).build();
    when(this.culoProperties.getRoom()).thenReturn(roomProperties);
    final var expiredLastActivity = Instant.now().minusSeconds(10_000);
    final var expiredRoomLobby = Instancio.of(RoomLobby.class)
        .set(field(RoomLobby::code), "OLD1")
        .set(field(RoomLobby::lastActivity), expiredLastActivity)
        .create();
    final var expiredRoom = Instancio.of(Room.class)
        .set(field(Room::roomLobby), expiredRoomLobby)
        .create();
    final var activeLastActivity = Instant.now();
    final var activeRoomLobby = Instancio.of(RoomLobby.class)
        .set(field(RoomLobby::code), "NEW1")
        .set(field(RoomLobby::lastActivity), activeLastActivity)
        .create();
    final var activeRoom = Instancio.of(Room.class)
        .set(field(Room::roomLobby), activeRoomLobby)
        .create();
    when(this.roomRetrievalPersistencePort.findAll()).thenReturn(List.of(expiredRoom, activeRoom));

    // When
    this.roomCleanupScheduler.purgeInactiveRooms();

    // Then
    verify(this.roomRetrievalPersistencePort, times(1)).findAll();
    verify(this.roomDeletionPersistencePort, times(1)).deleteByCode("OLD1");
    verify(this.roomDeletionPersistencePort, never()).deleteByCode("NEW1");
  }

  /**
	 * Test purge inactive rooms when all rooms active then no delete.
	 */
  @Test
  void testPurgeInactiveRooms_whenAllRoomsActive_thenNoDelete() {
    // Given
    final var roomProperties = CuloProperties.Room.builder().ttlHours(2).build();
    when(this.culoProperties.getRoom()).thenReturn(roomProperties);
    final var activeLastActivity = Instant.now();
    final var activeRoomLobby = Instancio.of(RoomLobby.class)
        .set(field(RoomLobby::lastActivity), activeLastActivity)
        .create();
    final var activeRoom = Instancio.of(Room.class)
        .set(field(Room::roomLobby), activeRoomLobby)
        .create();
    when(this.roomRetrievalPersistencePort.findAll()).thenReturn(List.of(activeRoom));

    // When
    this.roomCleanupScheduler.purgeInactiveRooms();

    // Then
    verify(this.roomRetrievalPersistencePort, times(1)).findAll();
    verify(this.roomDeletionPersistencePort, never()).deleteByCode(activeRoom.roomLobby().code());
  }
}
