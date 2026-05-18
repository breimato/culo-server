package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.Room;
import lombok.Builder;

@Builder
public record PassResult(
        Room room,
        String playerId,
        boolean roundEnded
) {
}
