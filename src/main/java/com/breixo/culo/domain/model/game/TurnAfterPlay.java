package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/**
 * The Record TurnAfterPlay.
 *
 * @param room              the room
 * @param gameFinished      the game finished
 * @param roundClosedByPlin the round closed by plin
 */
@Builder
public record TurnAfterPlay(
        Room room,
        boolean gameFinished,
        boolean roundClosedByPlin
) {
}
