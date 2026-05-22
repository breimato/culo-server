package com.breixo.culo.domain.command.game;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * The Record PassCommand.
 *
 * @param clientId the client id
 * @param roomCode the room code
 */
@Builder
public record PassCommand(
        @NotBlank String clientId,
        @NotBlank String roomCode
) {
}
