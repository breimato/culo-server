package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.command.room.CloseRoomCommand;
import com.breixo.culo.domain.model.room.Room;

/**
 * The Interface CloseRoomUseCase.
 */
public interface CloseRoomUseCase {

    /**
	 * Execute.
	 *
	 * @param closeRoomCommand the close room command
	 * @return the room
	 */
    Room execute(CloseRoomCommand closeRoomCommand);
}
