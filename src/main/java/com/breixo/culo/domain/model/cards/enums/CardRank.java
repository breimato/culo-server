package com.breixo.culo.domain.model.cards.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Jerarquía de poder de las cartas (de menor a mayor).
 * DOS &lt; CUATRO &lt; CINCO &lt; SEIS &lt; SIETE &lt; SOTA &lt; CABALLO &lt; REY &lt; TRES &lt; AS_OTRO &lt; AS_OROS
 */
@Getter
@RequiredArgsConstructor
public enum CardRank {

    /** The dos. */
    DOS(1),

    /** The cuatro. */
    CUATRO(2),

    /** The cinco. */
    CINCO(3),

    /** The seis. */
    SEIS(4),

    /** The siete. */
    SIETE(5),

    /** The sota. */
    SOTA(6),

    /** The caballo. */
    CABALLO(7),

    /** The rey. */
    REY(8),

    /** The tres. */
    TRES(9),

    /** The as otro. */
    AS_OTRO(10),

    /** The as oros. */
    AS_OROS(11);

    /** The power. */
    private final Integer power;
}
