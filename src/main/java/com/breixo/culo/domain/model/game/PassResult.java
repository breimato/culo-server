package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** The Record PassResult. */
@Builder
public record PassResult(
        Room room,
        String playerId,
        boolean roundClosed
) {
}
