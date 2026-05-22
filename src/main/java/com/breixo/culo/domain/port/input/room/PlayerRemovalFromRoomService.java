package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.model.room.Room;

/**
 * The Interface PlayerRemovalFromRoomService.
 */
public interface PlayerRemovalFromRoomService {

    /**
	 * Removes the player from game state.
	 *
	 * @param room     the room
	 * @param playerId the player id
	 * @return the room
	 */
    Room removePlayerFromGameState(Room room, String playerId);
}
