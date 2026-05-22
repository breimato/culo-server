package com.breixo.culo.domain.port.input.player;

import com.breixo.culo.domain.model.game.PlayerElimination;
import com.breixo.culo.domain.model.room.Room;

/** The Interface PlayerEliminationService. */
public interface PlayerEliminationService {

    /**
     * Register player out.
     *
     * @param room     the room
     * @param playerId the player id
     * @return the register player out result
     */
    PlayerElimination registerPlayerOut(Room room, String playerId);
}
