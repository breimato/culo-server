package com.breixo.culo.domain.port.input.swap;

import com.breixo.culo.domain.model.swap.CuloSwapVoteCast;
import com.breixo.culo.domain.model.room.Room;

/**
 * The Interface CuloService.
 */
public interface CuloService {

    /**
	 * Register vote.
	 *
	 * @param room     the room
	 * @param playerId the player id
	 * @param accept   the accept
	 * @return the culo swap vote cast
	 */
    CuloSwapVoteCast registerVote(Room room, String playerId, boolean accept);

    /**
	 * Checks if is swap approved.
	 *
	 * @param room the room
	 * @return true, if is swap approved
	 */
    boolean isSwapApproved(Room room);

    /**
	 * Apply swap.
	 *
	 * @param room the room
	 * @return the room
	 */
    Room applySwap(Room room);

    /**
	 * Clear swap.
	 *
	 * @param room the room
	 * @return the room
	 */
    Room clearSwap(Room room);

    /**
	 * Initiate swap.
	 *
	 * @param room           the room
	 * @param initiatorId    the initiator id
	 * @param targetPlayerId the target player id
	 * @return the room
	 */
    Room initiateSwap(Room room, String initiatorId, String targetPlayerId);
}
