package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.Play;
import com.breixo.culo.domain.model.Room;
import lombok.Builder;

@Builder
public record PlayResult(
        Room room,
        String playerId,
        Play play,
        boolean plin,
        boolean roundEnded,
        boolean gameEnded
) {
}
