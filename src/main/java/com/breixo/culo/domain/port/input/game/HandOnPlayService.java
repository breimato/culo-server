package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.game.HandAfterPlay;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;

/**
 * The Interface HandOnPlayService.
 */
public interface HandOnPlayService {

    /**
	 * Apply.
	 *
	 * @param room   the room
	 * @param player the player
	 * @param play   the play
	 * @return the hand after play
	 */
    HandAfterPlay apply(Room room, Player player, Play play);
}
