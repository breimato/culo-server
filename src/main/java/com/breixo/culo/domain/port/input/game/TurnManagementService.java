package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.model.room.Room;

/**
 * The Interface TurnManagementService.
 */
public interface TurnManagementService {

    /**
	 * Gets the next active player id.
	 *
	 * @param room the room
	 * @return the next active player id
	 */
    String getNextActivePlayerId(Room room);

    /**
	 * Advance turn.
	 *
	 * @param room    the room
	 * @param skipOne the skip one
	 * @return the room
	 */
    Room advanceTurn(Room room, boolean skipOne);

    /**
	 * Find first active player index.
	 *
	 * @param room the room
	 * @return the integer
	 */
    Integer findFirstActivePlayerIndex(Room room);

    /**
	 * Find next active player index after.
	 *
	 * @param room     the room
	 * @param playerId the player id
	 * @return the integer
	 */
    Integer findNextActivePlayerIndexAfter(Room room, String playerId);

    /**
	 * Ensure current player is active.
	 *
	 * @param room the room
	 * @return the room
	 */
    Room ensureCurrentPlayerIsActive(Room room);

    /**
	 * Finish round and set opener.
	 *
	 * @param room the room
	 * @return the room
	 */
    Room finishRoundAndSetOpener(Room room);
}
