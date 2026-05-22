package com.breixo.culo.domain.model.room;

import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/**
 * The Record GameSessionContext.
 *
 * @param room   the room
 * @param player the player
 */
@Builder
public record GameSessionContext(
        Room room,
        Player player
) {
}
