package com.breixo.culo.domain.port.input.player;

import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.PlayerRole;

import java.util.Map;
import java.util.Optional;

/** The Interface PlayerRoleService. */
public interface PlayerRoleService {

    /**
     * Gets the player id by role.
     *
     * @param room the room
     * @param role the role
     * @return the player id by role
     */
    Optional<String> getPlayerIdByRole(Room room, PlayerRole role);

    /**
     * Assign roles.
     *
     * @param room the room
     * @return the room
     */
    Room assignRoles(Room room);

    /**
     * Update player roles.
     *
     * @param room          the room
     * @param rolesByPlayer the roles by player
     * @return the room
     */
    Room updatePlayerRoles(Room room, Map<PlayerRole, String> rolesByPlayer);

    /**
     * Reset player roles.
     *
     * @param room the room
     * @return the room
     */
    Room resetPlayerRoles(Room room);

    /**
     * Capture exchange roles.
     *
     * @param room the room
     * @return the map
     */
    Map<PlayerRole, String> captureExchangeRoles(Room room);

    /**
     * Needs post deal exchange.
     *
     * @param rolesBeforeDeal the roles before deal
     * @return true, if successful
     */
    boolean needsPostDealExchange(Map<PlayerRole, String> rolesBeforeDeal);
}
