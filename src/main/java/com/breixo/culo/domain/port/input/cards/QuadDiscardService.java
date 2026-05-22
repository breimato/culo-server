package com.breixo.culo.domain.port.input.cards;

import com.breixo.culo.domain.model.cards.QuadDiscardApplied;
import com.breixo.culo.domain.model.room.Room;

/** The Interface QuadDiscardService. */
public interface QuadDiscardService {

    /**
     * Discard quads.
     *
     * @param room     the room
     * @param playerId the player id
     * @return the discard quads result
     */
    QuadDiscardApplied discardQuads(Room room, String playerId);

    /**
     * Discard quads for all players.
     *
     * @param room the room
     * @return the room
     */
    Room discardQuadsForAllPlayers(Room room);

    /**
     * Drain quad discards.
     *
     * @param room the room
     * @return the discard quads result
     */
    QuadDiscardApplied drainQuadDiscards(Room room);
}
