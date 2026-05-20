package com.breixo.culo.domain.port.output.room;

import jakarta.validation.constraints.NotNull;

/** The Interface Room Existence Persistence Port. */
public interface RoomExistencePersistencePort {

  /**
   * Exists by code.
   *
   * @param roomCode the room code.
   * @return true if a room with the given code exists.
   */
  boolean existsByCode(@NotNull String roomCode);
}
