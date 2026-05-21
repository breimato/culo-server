package com.breixo.culo.domain.port.input.play;

import com.breixo.culo.domain.model.play.PassResult;
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
