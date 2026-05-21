package com.breixo.culo.domain.port.output.room;

import jakarta.validation.constraints.NotNull;

/**
 * The Interface RoomExistencePersistencePort.
 */
public interface RoomExistencePersistencePort {

  /**
	 * Exists by code.
	 *
	 * @param roomCode the room code
	 * @return true, if successful
	 */
  boolean existsByCode(@NotNull String roomCode);
}
