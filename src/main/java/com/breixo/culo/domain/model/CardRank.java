package com.breixo.culo.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Jerarquía de poder de las cartas (de menor a mayor).
 * DOS < CUATRO < CINCO < SEIS < SIETE < SOTA < CABALLO < REY < TRES < AS_OTRO < AS_OROS
 */
@Getter
@RequiredArgsConstructor
public enum CardRank {

    DOS(1),
    CUATRO(2),
    CINCO(3),
    SEIS(4),
    SIETE(5),
    SOTA(6),
    CABALLO(7),
    REY(8),
    TRES(9),
    AS_OTRO(10),
    AS_OROS(11);

    /** The power. */
    private final int power;

    public static CardRank of(final Card card) {
        if (card.number() == 1) {
            return card.suit() == Suit.OROS ? AS_OROS : AS_OTRO;
        }
        return switch (card.number()) {
            case 2 -> DOS;
            case 3 -> TRES;
            case 4 -> CUATRO;
            case 5 -> CINCO;
            case 6 -> SEIS;
            case 7 -> SIETE;
            case 10 -> SOTA;
            case 11 -> CABALLO;
            case 12 -> REY;
            default -> throw new IllegalArgumentException("Número de carta inválido: " + card.number());
        };
    }

    public boolean isHigherOrEqualThan(final CardRank other) {
        return this.power >= other.power;
    }
}
