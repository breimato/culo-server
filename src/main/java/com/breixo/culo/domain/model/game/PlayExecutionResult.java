package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** The Record PlayExecutionResult. */
@Builder
public record PlayExecutionResult(
        Room room,
        boolean plin,
        boolean roundClosed,
        boolean gameFinished
) {
}
