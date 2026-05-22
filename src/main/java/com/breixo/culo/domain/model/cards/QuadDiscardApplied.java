package com.breixo.culo.domain.model.cards;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

import java.util.List;

/** The Record QuadDiscardApplied. */
@Builder
public record QuadDiscardApplied(
        Room room,
        List<QuadDiscardEvent> events
) {

    public QuadDiscardApplied {
        events = List.copyOf(events);
    }
}
