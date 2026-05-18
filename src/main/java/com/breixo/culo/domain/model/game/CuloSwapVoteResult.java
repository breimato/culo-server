package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.Room;
import lombok.Builder;

@Builder
public record CuloSwapVoteResult(
        Room room,
        boolean completed,
        boolean accepted
) {
}
