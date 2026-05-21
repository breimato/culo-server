package com.breixo.culo.domain.command.game;

import com.breixo.culo.domain.model.card.enums.Suit;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/** The Record CardInput. */
@Builder
public record CardInput(
        @NotNull Suit suit,
        @NotNull Integer number
) {
}
