package com.breixo.culo.domain.model.play;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** The Record PlayExecutionResult. */
@Builder
public record PlayExecutionResult(
        Room room,
        boolean plin,
        boolean roundEnded,
        boolean gameEnded
) {
}
