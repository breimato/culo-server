package com.breixo.culo.domain.model.room;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * The Record Room.
 */
@Builder(toBuilder = true)
public record Room(
        @NotNull RoomLobby roomLobby,
        @NotNull GameSession gameSession,
        @NotNull ExchangeState exchangeState,
        @NotNull CuloSwapState culoSwapState
) {
}
