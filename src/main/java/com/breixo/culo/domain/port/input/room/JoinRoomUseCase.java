package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.command.room.JoinRoomCommand;
import com.breixo.culo.domain.model.room.RoomJoinResult;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * The Interface JoinRoomUseCase.
 */
public interface JoinRoomUseCase {

  /**
	 * Execute.
	 *
	 * @param joinRoomCommand the join room command
	 * @return the room join result
	 */
  RoomJoinResult execute(@Valid @NotNull JoinRoomCommand joinRoomCommand);
}
