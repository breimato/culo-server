package com.breixo.culo.domain.service.player;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.player.HandManagementService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Class HandManagementServiceImpl.
 */
@Service
public class HandManagementServiceImpl implements HandManagementService {

    /** {@inheritDoc} */
    @Override
    public Room removeCardsFromHand(final Room room, final String playerId, final List<Card> cards) {

        final var hand = new ArrayList<>(room.gameSession().hands().getOrDefault(playerId, List.of()));
        hand.removeAll(cards);

        return this.updateHand(room, playerId, hand);
    }

    /** {@inheritDoc} */
    @Override
    public Room addCardsToHand(final Room room, final String playerId, final List<Card> cards) {

        final var hand = new ArrayList<>(room.gameSession().hands().getOrDefault(playerId, List.of()));
        hand.addAll(cards);

        return this.updateHand(room, playerId, hand);
    }

    /**
	 * Update hand.
	 *
	 * @param room     the room
	 * @param playerId the player id
	 * @param hand     the hand
	 * @return the room
	 */
    private Room updateHand(final Room room, final String playerId, final List<Card> hand) {

        final var hands = new HashMap<>(room.gameSession().hands());
        hands.put(playerId, List.copyOf(hand));

        return room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .hands(hands)
                        .build())
                .build();
    }
}
