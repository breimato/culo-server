package com.breixo.culo.domain.port.input.dealing;

import com.breixo.culo.domain.model.room.Room;

/** The Interface DealingCompletionService. */
public interface DealingCompletionService {

    /**
     * Execute.
     *
     * @param room the room
     * @return the room
     */
    Room execute(Room room);
}
