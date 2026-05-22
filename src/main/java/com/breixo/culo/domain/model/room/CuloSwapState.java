package com.breixo.culo.domain.model.room;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

/**
 * The Record CuloSwapState.
 *
 * @param initiatorId the initiator id
 * @param targetId    the target id
 * @param votes       the votes
 */
@Builder(toBuilder = true)
public record CuloSwapState(
        String initiatorId,
        String targetId,
        @NotNull Map<String, Boolean> votes
) {

    /**
	 * Instantiates a new culo swap state.
	 *
	 * @param initiatorId the initiator id
	 * @param targetId    the target id
	 * @param votes       the votes
	 */
    public CuloSwapState {
        votes = Map.copyOf(votes);
    }
}
