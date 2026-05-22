package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.command.room.CloseRoomCommand;
import com.breixo.culo.domain.model.room.Room;

/** Close room for all players (host only). */
public interface CloseRoomUseCase {

    /**
     * Deletes the room after validation. Returns the room snapshot for broadcasting.
     *
     * @param closeRoomCommand the close room command
     * @return the room before deletion
     */
    Room execute(CloseRoomCommand closeRoomCommand);
}
