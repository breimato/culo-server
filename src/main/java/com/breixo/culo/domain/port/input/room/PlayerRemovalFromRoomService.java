package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.model.room.Room;

/** Cleans game state when a player leaves mid-session. */
public interface PlayerRemovalFromRoomService {

    /**
     * Removes player data from hands, turn order and auxiliary state.
     *
     * @param room     the room
     * @param playerId the player id
     * @return the room
     */
    Room removePlayerFromGameState(Room room, String playerId);
}
