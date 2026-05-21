package com.breixo.culo.domain.model.outcome;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** The Record RoundCloseResult. */
@Builder
public record RoundCloseResult(
        Room room,
        boolean roundEnded
) {
}
