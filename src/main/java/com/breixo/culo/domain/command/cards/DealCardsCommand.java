package com.breixo.culo.domain.command.cards;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record DealCardsCommand(
        @NotBlank String clientId,
        @NotBlank String roomCode
) {
}
