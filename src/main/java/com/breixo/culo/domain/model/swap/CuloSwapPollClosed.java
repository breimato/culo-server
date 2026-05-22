package com.breixo.culo.domain.model.swap;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** Resultado de cerrar la votacion cuando todos han votado (antes de persistir). */
@Builder
public record CuloSwapPollClosed(
        Room room,
        boolean votingFinished,
        boolean swapAccepted
) {
}
