package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** The Record TurnAfterPlay. */
@Builder
public record TurnAfterPlay(
        Room room,
        boolean gameFinished,
        boolean roundClosedByPlin
) {
}
