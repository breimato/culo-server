package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.command.room.LeaveRoomCommand;
import com.breixo.culo.domain.model.room.Room;

import java.util.Optional;

/**
 * The Interface LeaveRoomUseCase.
 */
public interface LeaveRoomUseCase {

    /**
	 * Execute.
	 *
	 * @param leaveRoomCommand the leave room command
	 * @return the optional
	 */
    Optional<Room> execute(LeaveRoomCommand leaveRoomCommand);
}
