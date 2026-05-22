package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;

import java.util.Optional;

/**
 * The Interface PlayerLookupService.
 */
public interface PlayerLookupService {

    /**
	 * Find player by client id.
	 *
	 * @param room     the room
	 * @param clientId the client id
	 * @return the optional
	 */
    Optional<Player> findPlayerByClientId(Room room, String clientId);

    /**
	 * Find player by id.
	 *
	 * @param room     the room
	 * @param playerId the player id
	 * @return the optional
	 */
    Optional<Player> findPlayerById(Room room, String playerId);
}
