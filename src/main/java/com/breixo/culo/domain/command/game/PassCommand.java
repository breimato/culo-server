package com.breixo.culo.domain.command.game;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PassCommand(
        @NotBlank String clientId,
        @NotBlank String roomCode
) {
}
