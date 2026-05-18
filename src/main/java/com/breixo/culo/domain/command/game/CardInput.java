package com.breixo.culo.domain.command.game;

import com.breixo.culo.domain.model.Suit;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CardInput(
        @NotNull Suit suit,
        int number
) {
}
