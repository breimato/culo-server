package com.breixo.culo.domain.port.input.player;

import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.room.Room;

import java.util.List;

/** The Interface HandManagementService. */
public interface HandManagementService {

    /**
     * Remove cards from hand.
     *
     * @param room     the room
     * @param playerId the player id
     * @param cards    the cards
     * @return the room
     */
    Room removeCardsFromHand(Room room, String playerId, List<Card> cards);

    /**
     * Add cards to hand.
     *
     * @param room     the room
     * @param playerId the player id
     * @param cards    the cards
     * @return the room
     */
    Room addCardsToHand(Room room, String playerId, List<Card> cards);
}
