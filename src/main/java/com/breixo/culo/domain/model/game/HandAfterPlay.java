package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/**
 * The Record HandAfterPlay.
 *
 * @param room      the room
 * @param playFlags the play flags
 */
@Builder
public record HandAfterPlay(
        Room room,
        PlayTraits playFlags
) {
}
