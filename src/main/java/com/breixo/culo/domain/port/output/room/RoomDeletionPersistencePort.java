package com.breixo.culo.domain.port.output.room;

import jakarta.validation.constraints.NotNull;

/**
 * The Interface RoomDeletionPersistencePort.
 */
public interface RoomDeletionPersistencePort {

  /**
	 * Delete by code.
	 *
	 * @param roomCode the room code
	 */
  void deleteByCode(@NotNull String roomCode);
}
