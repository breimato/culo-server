package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.command.room.CreateRoomCommand;
import com.breixo.culo.domain.model.room.Room;

/**
 * The Interface RoomFactoryService.
 */
public interface RoomFactoryService {

    /**
	 * Creates the empty room.
	 *
	 * @param createRoomCommand the create room command
	 * @param roomCode          the room code
	 * @param hostPlayerId      the host player id
	 * @return the room
	 */
    Room createEmptyRoom(CreateRoomCommand createRoomCommand, String roomCode, String hostPlayerId);
}
