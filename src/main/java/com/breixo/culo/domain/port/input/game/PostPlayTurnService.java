package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.model.game.PlayTraits;
import com.breixo.culo.domain.model.game.TurnAfterPlay;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;

/**
 * The Interface PostPlayTurnService.
 */
public interface PostPlayTurnService {

    /**
	 * Apply.
	 *
	 * @param room      the room
	 * @param player    the player
	 * @param playFlags the play flags
	 * @return the turn after play
	 */
    TurnAfterPlay apply(Room room, Player player, PlayTraits playFlags);
}
