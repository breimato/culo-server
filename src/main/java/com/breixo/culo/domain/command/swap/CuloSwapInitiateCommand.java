package com.breixo.culo.domain.command.swap;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * The Record CuloSwapInitiateCommand.
 *
 * @param clientId       the client id
 * @param roomCode       the room code
 * @param targetPlayerId the target player id
 */
@Builder
public record CuloSwapInitiateCommand(
        @NotBlank String clientId,
        @NotBlank String roomCode,
        @NotBlank String targetPlayerId
) {
}
