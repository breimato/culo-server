package com.breixo.culo.domain.port.output.room;

import com.breixo.culo.domain.model.Room;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * The Interface RoomSavePersistencePort.
 */
public interface RoomSavePersistencePort {

  /**
	 * Save.
	 *
	 * @param room the room
	 * @return the room
	 */
  Room save(@Valid @NotNull Room room);
}
