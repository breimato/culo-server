package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.model.game.RoundClosure;
import com.breixo.culo.domain.model.room.Room;

/** The Interface RoundClosingService. */
public interface RoundClosingService {

    /**
     * Close round if others all passed.
     *
     * @param room the room
     * @return the round close result
     */
    RoundClosure closeRoundIfOthersAllPassed(Room room);
}
