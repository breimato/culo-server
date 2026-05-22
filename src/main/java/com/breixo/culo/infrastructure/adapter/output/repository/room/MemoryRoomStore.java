package com.breixo.culo.infrastructure.adapter.output.repository.room;

import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.room.RoomMembershipService;
import com.breixo.culo.domain.port.output.room.RoomDeletionPersistencePort;
import com.breixo.culo.domain.port.output.room.RoomExistencePersistencePort;
import com.breixo.culo.domain.port.output.room.RoomRetrievalPersistencePort;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** The Class MemoryRoomStore. */
@Component
@Profile("memory")
@RequiredArgsConstructor
public class MemoryRoomStore implements
    RoomSavePersistencePort,
    RoomRetrievalPersistencePort,
    RoomExistencePersistencePort,
    RoomDeletionPersistencePort {

  /** The room membership service. */
  private final RoomMembershipService roomMembershipService;

  private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

  /** {@inheritDoc} */
  @Override
  public Room save(final Room room) {
    final var touchedRoom = this.roomMembershipService.touch(room);
    this.rooms.put(touchedRoom.roomLobby().code(), touchedRoom);
    return touchedRoom;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Room> findByCode(final String roomCode) {
    return Optional.ofNullable(this.rooms.get(roomCode));
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Room> findAll() {
    return this.rooms.values();
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByCode(final String roomCode) {
    return this.rooms.containsKey(roomCode);
  }

  /** {@inheritDoc} */
  @Override
  public void deleteByCode(final String roomCode) {
    this.rooms.remove(roomCode);
  }
}
