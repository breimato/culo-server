package com.breixo.culo.domain.model.swap;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/**
 * The Record CuloSwapVoteResponse.
 *
 * @param room           the room
 * @param votingFinished the voting finished
 * @param swapAccepted   the swap accepted
 */
@Builder
public record CuloSwapVoteResponse(
        Room room,
        boolean votingFinished,
        boolean swapAccepted
) {
}
