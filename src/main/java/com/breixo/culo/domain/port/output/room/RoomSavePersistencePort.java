package com.breixo.culo.domain.port.output.room;

import com.breixo.culo.domain.model.Room;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** The Interface Room Save Persistence Port. */
public interface RoomSavePersistencePort {

  /**
   * Save.
   *
   * @param room the room.
   * @return the saved room.
   */
  Room save(@Valid @NotNull Room room);
}
