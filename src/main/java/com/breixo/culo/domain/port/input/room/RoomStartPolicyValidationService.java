package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;

/** The Interface RoomStartPolicyValidationService. */
public interface RoomStartPolicyValidationService {

    /**
     * Validate can start.
     *
     * @param room   the room
     * @param player the player
     */
    void validateCanStart(Room room, Player player);
}
