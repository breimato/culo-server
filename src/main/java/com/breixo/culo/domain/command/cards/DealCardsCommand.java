package com.breixo.culo.domain.command.cards;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * The Record DealCardsCommand.
 *
 * @param clientId the client id
 * @param roomCode the room code
 */
@Builder
public record DealCardsCommand(
        @NotBlank String clientId,
        @NotBlank String roomCode
) {
}
