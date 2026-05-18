package com.breixo.culo.domain.port.output.room;

import com.breixo.culo.domain.model.Room;

import java.util.Collection;
import java.util.Optional;

public interface RoomPersistencePort {

  Room save(Room room);

  Optional<Room> findByCode(String roomCode);

  boolean existsByCode(String roomCode);

  void deleteByCode(String roomCode);

  Collection<Room> findAll();
}
