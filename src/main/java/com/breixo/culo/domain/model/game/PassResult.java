package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/**
 * The Record PassResult.
 *
 * @param room        the room
 * @param playerId    the player id
 * @param roundClosed the round closed
 */
@Builder
public record PassResult(
        Room room,
        String playerId,
        boolean roundClosed
) {
}
