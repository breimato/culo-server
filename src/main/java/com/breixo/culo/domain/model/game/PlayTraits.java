package com.breixo.culo.domain.model.game;

import lombok.Builder;

/**
 * The Record PlayTraits.
 *
 * @param plin     the plin
 * @param isAsOros the is as oros
 */
@Builder
public record PlayTraits(
        boolean plin,
        boolean isAsOros
) {
}
