package com.breixo.culo.domain.model.cards;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

import java.util.List;

/**
 * The Record QuadDiscardApplied.
 *
 * @param room   the room
 * @param events the events
 */
@Builder
public record QuadDiscardApplied(
        Room room,
        List<QuadDiscardEvent> events
) {

    /**
	 * Instantiates a new quad discard applied.
	 *
	 * @param room   the room
	 * @param events the events
	 */
    public QuadDiscardApplied {
        events = List.copyOf(events);
    }
}
