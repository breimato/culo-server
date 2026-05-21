package com.breixo.culo.domain.model.quad;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

import java.util.List;

/** The Record DiscardQuadsResult. */
@Builder
public record DiscardQuadsResult(
        Room room,
        List<QuadDiscardEvent> events
) {

    public DiscardQuadsResult {
        events = List.copyOf(events);
    }
}
