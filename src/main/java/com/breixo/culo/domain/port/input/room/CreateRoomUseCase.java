package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.command.room.CreateRoomCommand;
import com.breixo.culo.domain.model.room.RoomJoinResult;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** The Interface Create Room Use Case. */
public interface CreateRoomUseCase {

  /**
   * Execute.
   *
   * @param createRoomCommand the create room command.
   * @return the room join result.
   */
  RoomJoinResult execute(@Valid @NotNull CreateRoomCommand createRoomCommand);
}
