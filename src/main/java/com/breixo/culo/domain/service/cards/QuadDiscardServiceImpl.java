package com.breixo.culo.domain.service.cards;

import com.breixo.culo.domain.constants.GameConstants;
import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.QuadDiscardApplied;
import com.breixo.culo.domain.model.cards.QuadInHand;
import com.breixo.culo.domain.model.cards.QuadDiscardEvent;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.cards.QuadDiscardService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The Class QuadDiscardServiceImpl.
 */
@Service
public class QuadDiscardServiceImpl implements QuadDiscardService {

    /** {@inheritDoc} */
    @Override
    public QuadDiscardApplied discardQuads(final Room room, final String playerId) {

        final var hand = room.gameSession().hands().get(playerId);

        if (Objects.isNull(hand) || hand.isEmpty()) {
            return this.emptyDiscardResult(room);
        }

        final var quadInHand = this.detectQuadEvents(hand, playerId);
        final var events = quadInHand.events();

        if (events.isEmpty()) {
            return this.emptyDiscardResult(room);
        }

        final var updatedHand = quadInHand.updatedHand();
        return this.applyQuadDiscardToRoom(room, playerId, updatedHand, events);
    }

    /** {@inheritDoc} */
    @Override
    public Room discardQuadsForAllPlayers(final Room room) {

        var roomAfterDiscard = room;

        for (final var playerId : roomAfterDiscard.gameSession().playerOrder()) {
            final var discardResult = this.discardQuads(roomAfterDiscard, playerId);
            roomAfterDiscard = discardResult.room();
        }

        return roomAfterDiscard;
    }

    /** {@inheritDoc} */
    @Override
    public QuadDiscardApplied drainQuadDiscards(final Room room) {

        final var drained = List.copyOf(room.gameSession().pendingQuadDiscards());
        final var roomAfterDrain = room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .pendingQuadDiscards(List.of())
                        .build())
                .build();

        return QuadDiscardApplied.builder()
                .room(roomAfterDrain)
                .events(drained)
                .build();
    }

    /**
	 * Empty discard result.
	 *
	 * @param room the room
	 * @return the quad discard applied
	 */
    private QuadDiscardApplied emptyDiscardResult(final Room room) {

        return QuadDiscardApplied.builder()
                .room(room)
                .events(List.of())
                .build();
    }

    /**
	 * Detect quad events.
	 *
	 * @param hand     the hand
	 * @param playerId the player id
	 * @return the quad in hand
	 */
    private QuadInHand detectQuadEvents(final List<Card> hand, final String playerId) {

        final var cardsByNumber = hand.stream()
                .collect(Collectors.groupingBy(Card::number));

        final var events = new ArrayList<QuadDiscardEvent>();
        final var updatedHand = new ArrayList<>(hand);

        for (final var entry : cardsByNumber.entrySet()) {
            this.discardQuadIfPresent(playerId, entry.getKey(), entry.getValue(), updatedHand, events);
        }

        return QuadInHand.builder()
                .updatedHand(List.copyOf(updatedHand))
                .events(events)
                .build();
    }

    /**
	 * Discard quad if present.
	 *
	 * @param playerId      the player id
	 * @param cardNumber    the card number
	 * @param cardsOfNumber the cards of number
	 * @param updatedHand   the updated hand
	 * @param events        the events
	 */
    private void discardQuadIfPresent(
            final String playerId,
            final Integer cardNumber,
            final List<Card> cardsOfNumber,
            final List<Card> updatedHand,
            final List<QuadDiscardEvent> events) {

        if (cardsOfNumber.size() < GameConstants.QUAD_SIZE) {
            return;
        }

        final var quadCards = new ArrayList<>(cardsOfNumber);
        updatedHand.removeIf(quadCards::contains);
        events.add(QuadDiscardEvent.builder()
                .playerId(playerId)
                .value(cardNumber)
                .cards(List.copyOf(quadCards))
                .build());
    }

    /**
	 * Apply quad discard to room.
	 *
	 * @param room        the room
	 * @param playerId    the player id
	 * @param updatedHand the updated hand
	 * @param events      the events
	 * @return the quad discard applied
	 */
    private QuadDiscardApplied applyQuadDiscardToRoom(
            final Room room,
            final String playerId,
            final List<Card> updatedHand,
            final List<QuadDiscardEvent> events) {

        final var hands = new HashMap<>(room.gameSession().hands());
        hands.put(playerId, updatedHand);

        final var pendingQuadDiscards = new ArrayList<>(room.gameSession().pendingQuadDiscards());
        pendingQuadDiscards.addAll(events);

        final var roomAfterDiscard = room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .hands(hands)
                        .pendingQuadDiscards(pendingQuadDiscards)
                        .build())
                .build();

        return QuadDiscardApplied.builder()
                .room(roomAfterDiscard)
                .events(events)
                .build();
    }
}
