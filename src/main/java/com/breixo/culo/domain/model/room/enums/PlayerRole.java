package com.breixo.culo.domain.model.room.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The Enum PlayerRole.
 */
@Getter
@RequiredArgsConstructor
public enum PlayerRole {

    /** The none. */
    NONE(0),

    /** The ganador. */
    GANADOR(1),

    /** The subcampeon. */
    SUBCAMPEON(2),

    /** The penultimo. */
    PENULTIMO(3),

    /** The culo. */
    CULO(4);

    /** The id. */
    private final Integer id;
}
