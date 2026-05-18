package com.breixo.culo.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

/**
 * Carta de baraja española (40 cartas: 1–7, 10, 11, 12).
 */
@Builder
public record Card(
        @NotNull Suit suit,
        int number
) {

    private static final Set<Integer> VALID_NUMBERS = Set.of(1, 2, 3, 4, 5, 6, 7, 10, 11, 12);

    public Card {
        if (!VALID_NUMBERS.contains(number)) {
            throw new IllegalArgumentException("Número de carta inválido: " + number);
        }
    }
}
