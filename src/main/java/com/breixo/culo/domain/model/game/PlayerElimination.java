package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** The Record PlayerElimination. */
@Builder
public record PlayerElimination(
        Room room,
        boolean gameFinished
) {
}
