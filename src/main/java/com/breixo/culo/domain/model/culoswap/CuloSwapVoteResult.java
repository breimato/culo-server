package com.breixo.culo.domain.model.culoswap;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** The Record CuloSwapVoteResult. */
@Builder
public record CuloSwapVoteResult(
        Room room,
        boolean completed,
        boolean accepted
) {
}
