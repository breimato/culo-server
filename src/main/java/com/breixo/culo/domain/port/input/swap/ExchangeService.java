package com.breixo.culo.domain.port.input.swap;

import com.breixo.culo.domain.command.swap.ExchangeGiveCommand;
import com.breixo.culo.domain.model.room.Room;

/** The Interface ExchangeService. */
public interface ExchangeService {

    /**
     * Process give.
     *
     * @param room                the room
     * @param playerId            the player id
     * @param exchangeGiveCommand the exchange give command
     * @return the room
     */
    Room processGive(Room room, String playerId, ExchangeGiveCommand exchangeGiveCommand);

    /**
     * Finalize if complete.
     *
     * @param room the room
     * @return the room
     */
    Room finalizeIfComplete(Room room);
}
