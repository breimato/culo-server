package com.breixo.culo.domain.port.input.exchange;

import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;

import java.util.List;

/** The Interface ExchangePolicyValidationService. */
public interface ExchangePolicyValidationService {

    /**
     * Validate not already done.
     *
     * @param room   the room
     * @param player the player
     */
    void validateNotAlreadyDone(Room room, Player player);

    /**
     * Validate role can exchange.
     *
     * @param player the player
     */
    void validateRoleCanExchange(Player player);

    /**
     * Validate give cards count.
     *
     * @param player the player
     * @param cards  the cards
     */
    void validateGiveCardsCount(Player player, List<Card> cards);
}
