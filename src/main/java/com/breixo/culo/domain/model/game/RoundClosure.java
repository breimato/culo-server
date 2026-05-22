package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** The Record RoundClosure. */
@Builder
public record RoundClosure(
        Room room,
        boolean roundClosed
) {
}
