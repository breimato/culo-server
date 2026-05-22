package com.breixo.culo.domain.model.swap;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/** Estado de la sala tras registrar el voto de un jugador en el intercambio de culo. */
@Builder
public record CuloSwapVoteCast(
        Room room,
        boolean allPlayersHaveVoted
) {
}
