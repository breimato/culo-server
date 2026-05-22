package com.breixo.culo.domain.model.swap;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** Respuesta del use case de voto: sala guardada y si la votacion termino con swap aceptado. */
@Builder
public record CuloSwapVoteResponse(
        Room room,
        boolean votingFinished,
        boolean swapAccepted
) {
}
