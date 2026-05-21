package com.breixo.culo.domain.model.culoswap;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** The Record CuloSwapVoteRegistrationResult. */
@Builder
public record CuloSwapVoteRegistrationResult(
        Room room,
        boolean allVoted
) {
}
