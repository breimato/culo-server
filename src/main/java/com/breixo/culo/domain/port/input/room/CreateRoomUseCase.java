package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.command.room.CreateRoomCommand;
import com.breixo.culo.domain.model.room.RoomJoinResult;

public interface CreateRoomUseCase {

  RoomJoinResult execute(CreateRoomCommand createRoomCommand);
}
