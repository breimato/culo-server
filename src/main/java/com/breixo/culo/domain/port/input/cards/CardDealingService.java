package com.breixo.culo.domain.port.input.cards;

import com.breixo.culo.domain.model.room.Room;

/**
 * The Interface CardDealingService.
 */
public interface CardDealingService {

    /**
	 * Deal cards.
	 *
	 * @param room the room
	 * @return the room
	 */
    Room dealCards(Room room);

    /**
	 * Find two of oros player index.
	 *
	 * @param room the room
	 * @return the integer
	 */
    Integer findTwoOfOrosPlayerIndex(Room room);

    /**
	 * Transfer highest cards.
	 *
	 * @param room       the room
	 * @param giverId    the giver id
	 * @param receiverId the receiver id
	 * @param count      the count
	 * @return the room
	 */
    Room transferHighestCards(Room room, String giverId, String receiverId, Integer count);
}
