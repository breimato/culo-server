package com.breixo.culo.domain.model.outcome;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** The Record RegisterPlayerOutResult. */
@Builder
public record RegisterPlayerOutResult(
        Room room,
        boolean gameEnded
) {
}
