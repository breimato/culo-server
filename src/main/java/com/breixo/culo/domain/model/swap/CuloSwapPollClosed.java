package com.breixo.culo.domain.model.swap;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/**
 * The Record CuloSwapPollClosed.
 *
 * @param room           the room
 * @param votingFinished the voting finished
 * @param swapAccepted   the swap accepted
 */
@Builder
public record CuloSwapPollClosed(
        Room room,
        boolean votingFinished,
        boolean swapAccepted
) {
}
