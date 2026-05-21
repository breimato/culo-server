package com.breixo.culo.domain.port.input.turn;

import com.breixo.culo.domain.model.outcome.RoundCloseResult;
import com.breixo.culo.domain.model.room.Room;

/** The Interface RoundCloseService. */
public interface RoundCloseService {

    /**
     * Close round if others all passed.
     *
     * @param room the room
     * @return the round close result
     */
    RoundCloseResult closeRoundIfOthersAllPassed(Room room);
}
