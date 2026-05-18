package com.breixo.culo.infrastructure.adapter.output.repository.room;

import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryRoomPersistenceRepository implements RoomPersistencePort {

  private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

  @Override
  public Room save(final Room room) {
    room.touch();
    this.rooms.put(room.getCode(), room);
    return room;
  }

  @Override
  public Optional<Room> findByCode(final String roomCode) {
    return Optional.ofNullable(this.rooms.get(roomCode));
  }

  @Override
  public boolean existsByCode(final String roomCode) {
    return this.rooms.containsKey(roomCode);
  }

  @Override
  public void deleteByCode(final String roomCode) {
    this.rooms.remove(roomCode);
  }

  @Override
  public Collection<Room> findAll() {
    return this.rooms.values();
  }
}
