package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/**
 * The Record PlayerEliminationCheck.
 *
 * @param room                the room
 * @param playerWasEliminated the player was eliminated
 * @param gameFinished        the game finished
 */
@Builder
public record PlayerEliminationCheck(
        Room room,
        boolean playerWasEliminated,
        boolean gameFinished
) {
}
