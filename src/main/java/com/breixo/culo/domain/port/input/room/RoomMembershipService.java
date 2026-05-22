package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import jakarta.validation.constraints.NotNull;

/** The Interface RoomMembershipService. */
public interface RoomMembershipService {

    /**
     * Adds the player.
     *
     * @param room   the room
     * @param player the player
     * @return the room
     */
    Room addPlayer(@NotNull Room room, @NotNull Player player);

    /**
     * Reconnect player.
     *
     * @param room   the room
     * @param player the player
     * @return the room
     */
    Room reconnectPlayer(Room room, Player player);

    /**
     * Touch.
     *
     * @param room the room
     * @return the room
     */
    Room touch(Room room);

    /**
     * Removes a player from the lobby and reassigns host if needed.
     *
     * @param room   the room
     * @param player the player
     * @return the room
     */
    Room removePlayer(Room room, Player player);
}
