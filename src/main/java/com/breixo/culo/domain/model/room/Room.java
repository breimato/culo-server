package com.breixo.culo.domain.model.room;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * The Record Room.
 *
 * @param roomLobby     the room lobby
 * @param gameSession   the game session
 * @param exchangeState the exchange state
 * @param culoSwapState the culo swap state
 */
@Builder(toBuilder = true)
public record Room(
        @NotNull RoomLobby roomLobby,
        @NotNull GameSession gameSession,
        @NotNull ExchangeState exchangeState,
        @NotNull CuloSwapState culoSwapState
) {
}
