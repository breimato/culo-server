package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.game.PlayExecutionResult;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;

/**
 * The Interface PlayExecutionService.
 */
public interface PlayExecutionService {

    /**
	 * Execute.
	 *
	 * @param room   the room
	 * @param player the player
	 * @param play   the play
	 * @return the play execution result
	 */
    PlayExecutionResult execute(Room room, Player player, Play play);
}
