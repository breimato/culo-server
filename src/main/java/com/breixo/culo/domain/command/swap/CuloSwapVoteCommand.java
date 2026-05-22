package com.breixo.culo.domain.command.swap;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * The Record CuloSwapVoteCommand.
 *
 * @param clientId the client id
 * @param roomCode the room code
 * @param accept   the accept
 */
@Builder
public record CuloSwapVoteCommand(
        @NotBlank String clientId,
        @NotBlank String roomCode,
        boolean accept
) {
}
