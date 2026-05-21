package com.breixo.culo.domain.model.play;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** The Record PassResult. */
@Builder
public record PassResult(
        Room room,
        String playerId,
        boolean roundEnded
) {
}
