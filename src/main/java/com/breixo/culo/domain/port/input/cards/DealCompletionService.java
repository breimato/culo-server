package com.breixo.culo.domain.port.input.cards;

import com.breixo.culo.domain.model.room.Room;

/** The Interface DealCompletionService. */
public interface DealCompletionService {

    /**
     * Execute.
     *
     * @param room the room
     * @return the room
     */
    Room execute(Room room);
}
