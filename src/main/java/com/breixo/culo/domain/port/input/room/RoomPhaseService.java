package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;

/** The Interface RoomPhaseService. */
public interface RoomPhaseService {

    /**
     * Require phase.
     *
     * @param room          the room
     * @param expectedPhase the expected phase
     */
    void requirePhase(Room room, GamePhase expectedPhase);

    /**
     * Require lobby phase.
     *
     * @param room the room
     */
    void requireLobbyPhase(Room room);

    /**
     * With phase.
     *
     * @param room      the room
     * @param gamePhase the game phase
     * @return the room
     */
    Room withPhase(Room room, GamePhase gamePhase);
}
