package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.command.room.JoinRoomCommand;
import com.breixo.culo.domain.model.room.RoomJoinResult;

public interface JoinRoomUseCase {

  RoomJoinResult execute(JoinRoomCommand joinRoomCommand);
}
