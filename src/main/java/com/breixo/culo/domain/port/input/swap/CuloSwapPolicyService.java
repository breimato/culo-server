package com.breixo.culo.domain.port.input.swap;

import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;

/**
 * The Interface CuloSwapPolicyService.
 */
public interface CuloSwapPolicyService {

    /**
	 * Validate initiator.
	 *
	 * @param player the player
	 */
    void validateInitiator(Player player);

    /**
	 * Validate no active swap.
	 *
	 * @param room the room
	 */
    void validateNoActiveSwap(Room room);

    /**
	 * Validate not already voted.
	 *
	 * @param room   the room
	 * @param player the player
	 */
    void validateNotAlreadyVoted(Room room, Player player);

    /**
	 * Validate target exists.
	 *
	 * @param room           the room
	 * @param targetPlayerId the target player id
	 */
    void validateTargetExists(Room room, String targetPlayerId);
}
