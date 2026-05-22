package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.cards.CardInput;
import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.room.Room;

import java.util.List;

/** The Interface PlayBuilderService. */
public interface PlayBuilderService {

    /**
     * Build play.
     *
     * @param cards the cards
     * @return the play
     */
    Play buildPlay(List<Card> cards);

    /**
     * To cards.
     *
     * @param cardInputs the card inputs
     * @param room       the room
     * @param playerId   the player id
     * @return the list
     */
    List<Card> toCards(List<CardInput> cardInputs, Room room, String playerId);
}
