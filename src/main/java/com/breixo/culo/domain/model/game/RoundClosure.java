package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/**
 * The Record RoundClosure.
 *
 * @param room        the room
 * @param roundClosed the round closed
 */
@Builder
public record RoundClosure(
        Room room,
        boolean roundClosed
) {
}
