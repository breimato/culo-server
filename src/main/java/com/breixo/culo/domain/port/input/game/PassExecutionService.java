package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.model.game.PassResult;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;

/** The Interface PassExecutionService. */
public interface PassExecutionService {

    /**
     * Execute.
     *
     * @param room   the room
     * @param player the player
     * @return the pass result
     */
    PassResult execute(Room room, Player player);
}
