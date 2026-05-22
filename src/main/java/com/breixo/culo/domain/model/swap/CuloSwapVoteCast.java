package com.breixo.culo.domain.model.swap;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/**
 * The Record CuloSwapVoteCast.
 *
 * @param room                the room
 * @param allPlayersHaveVoted the all players have voted
 */
@Builder
public record CuloSwapVoteCast(
        Room room,
        boolean allPlayersHaveVoted
) {
}
