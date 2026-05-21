package com.breixo.culo.domain.port.input.player;

import com.breixo.culo.domain.model.outcome.RegisterPlayerOutResult;
import com.breixo.culo.domain.model.room.Room;

/** The Interface PlayerOutcomeService. */
public interface PlayerOutcomeService {

    /**
     * Gets the active player count.
     *
     * @param room the room
     * @return the active player count
     */
    Integer getActivePlayerCount(Room room);

    /**
     * Register player out.
     *
     * @param room     the room
     * @param playerId the player id
     * @return the register player out result
     */
    RegisterPlayerOutResult registerPlayerOut(Room room, String playerId);
}
