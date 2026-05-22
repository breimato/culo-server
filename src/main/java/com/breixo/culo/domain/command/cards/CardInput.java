package com.breixo.culo.domain.command.cards;

import com.breixo.culo.domain.model.cards.enums.Suit;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * The Record CardInput.
 *
 * @param suit   the suit
 * @param number the number
 */
@Builder
public record CardInput(
        @NotNull Suit suit,
        @NotNull Integer number
) {
}
