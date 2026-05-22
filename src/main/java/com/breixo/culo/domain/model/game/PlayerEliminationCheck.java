package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** The Record PlayerEliminationCheck. */
@Builder
public record PlayerEliminationCheck(
        Room room,
        boolean playerWasEliminated,
        boolean gameFinished
) {
}
