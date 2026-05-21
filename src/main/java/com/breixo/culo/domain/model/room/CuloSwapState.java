package com.breixo.culo.domain.model.room;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

/**
 * The Record CuloSwapState.
 */
@Builder(toBuilder = true)
public record CuloSwapState(
        String initiatorId,
        String targetId,
        @NotNull Map<String, Boolean> votes
) {

    public CuloSwapState {
        votes = Map.copyOf(votes);
    }
}
