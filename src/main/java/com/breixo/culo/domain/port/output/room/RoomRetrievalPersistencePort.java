package com.breixo.culo.domain.port.output.room;

import com.breixo.culo.domain.model.Room;

import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Optional;

/** The Interface Room Retrieval Persistence Port. */
public interface RoomRetrievalPersistencePort {

  /**
   * Find by code.
   *
   * @param roomCode the room code.
   * @return the room, or empty if not found.
   */
  Optional<Room> findByCode(@NotNull String roomCode);

  /**
   * Find all.
   *
   * @return all persisted rooms.
   */
  Collection<Room> findAll();
}
