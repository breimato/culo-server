package com.breixo.culo.domain.model.game;

import lombok.Builder;

/** The Record PlayTraits. */
@Builder
public record PlayTraits(
        boolean plin,
        boolean isAsOros
) {
}
