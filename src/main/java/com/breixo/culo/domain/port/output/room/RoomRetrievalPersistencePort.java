package com.breixo.culo.domain.port.output.room;

import com.breixo.culo.domain.model.Room;

import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * The Interface RoomRetrievalPersistencePort.
 */
public interface RoomRetrievalPersistencePort {

  /**
	 * Find by code.
	 *
	 * @param roomCode the room code
	 * @return the optional
	 */
  Optional<Room> findByCode(@NotNull String roomCode);

  /**
	 * Find all.
	 *
	 * @return the collection
	 */
  Collection<Room> findAll();
}
