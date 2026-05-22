package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/**
 * The Record PlayerElimination.
 *
 * @param room         the room
 * @param gameFinished the game finished
 */
@Builder
public record PlayerElimination(
        Room room,
        boolean gameFinished
) {
}
