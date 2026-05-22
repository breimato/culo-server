package com.breixo.culo.domain.model.room;

import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** The Record GameSessionContext. */
@Builder
public record GameSessionContext(
        Room room,
        Player player
) {
}
