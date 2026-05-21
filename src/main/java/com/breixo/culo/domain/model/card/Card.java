package com.breixo.culo.domain.model.card;

import com.breixo.culo.domain.model.card.enums.Suit;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/** The Record Card. */
@Builder(toBuilder = true)
public record Card(
        @NotNull Suit suit,
        @NotNull Integer number
) {
}
