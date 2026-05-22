package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.command.room.LeaveRoomCommand;
import com.breixo.culo.domain.model.room.Room;

import java.util.Optional;

/** Leave room use case. */
public interface LeaveRoomUseCase {

    /**
     * Removes the player from the room. Empty if the room was deleted.
     *
     * @param leaveRoomCommand the leave room command
     * @return the updated room, or empty when the room no longer exists
     */
    Optional<Room> execute(LeaveRoomCommand leaveRoomCommand);
}
