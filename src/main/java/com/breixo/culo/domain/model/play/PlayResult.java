package com.breixo.culo.domain.model.play;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** The Record PlayResult. */
@Builder
public record PlayResult(
        Room room,
        String playerId,
        Play play,
        boolean plin,
        boolean roundEnded,
        boolean gameEnded
) {
}
