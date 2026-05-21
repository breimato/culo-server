package com.breixo.culo.domain.service.quad;

import com.breixo.culo.domain.constants.GameConstants;
import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.quad.DiscardQuadsResult;
import com.breixo.culo.domain.model.quad.QuadDiscardEvent;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.quad.QuadDiscardService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** The Class QuadDiscardServiceImpl. */
@Service
public class QuadDiscardServiceImpl implements QuadDiscardService {

    /** {@inheritDoc} */
    @Override
    public DiscardQuadsResult discardQuads(final Room room, final String playerId) {

        final var hand = room.gameSession().hands().get(playerId);
        if (Objects.isNull(hand) || hand.isEmpty()) {
            return DiscardQuadsResult.builder()
                    .room(room)
                    .events(List.of())
                    .build();
        }

        final var groups = hand.stream()
                .collect(Collectors.groupingBy(Card::number));

        final var events = new ArrayList<QuadDiscardEvent>();
        final var updatedHand = new ArrayList<>(hand);
        for (final var entry : groups.entrySet()) {
            if (entry.getValue().size() >= GameConstants.QUAD_SIZE) {
                final var quadCards = new ArrayList<>(entry.getValue());
                updatedHand.removeIf(quadCards::contains);
                events.add(QuadDiscardEvent.builder()
                        .playerId(playerId)
                        .value(entry.getKey())
                        .cards(List.copyOf(quadCards))
                        .build());
            }
        }

        if (events.isEmpty()) {
            return DiscardQuadsResult.builder()
                    .room(room)
                    .events(List.of())
                    .build();
        }

        final var hands = new HashMap<>(room.gameSession().hands());
        hands.put(playerId, List.copyOf(updatedHand));
        final var pendingQuadDiscards = new ArrayList<>(room.gameSession().pendingQuadDiscards());
        pendingQuadDiscards.addAll(events);

        final var roomAfterDiscard = room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .hands(hands)
                        .pendingQuadDiscards(pendingQuadDiscards)
                        .build())
                .build();

        return DiscardQuadsResult.builder()
                .room(roomAfterDiscard)
                .events(events)
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public Room discardQuadsForAllPlayers(final Room room) {

        var roomAfterDiscard = room;
        for (final var playerId : roomAfterDiscard.gameSession().playerOrder()) {
            roomAfterDiscard = this.discardQuads(roomAfterDiscard, playerId).room();
        }
        return roomAfterDiscard;
    }

    /** {@inheritDoc} */
    @Override
    public DiscardQuadsResult drainQuadDiscards(final Room room) {

        final var drained = List.copyOf(room.gameSession().pendingQuadDiscards());
        final var roomAfterDrain = room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .pendingQuadDiscards(List.of())
                        .build())
                .build();
        return DiscardQuadsResult.builder()
                .room(roomAfterDrain)
                .events(drained)
                .build();
    }
}
