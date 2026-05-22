package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.GameSessionContext;

import java.util.List;

/**
 * The Interface PlayPolicyService.
 */
public interface PlayPolicyService {

    /**
	 * Validate player can play.
	 *
	 * @param gameSessionContext the game session context
	 */
    void validatePlayerCanPlay(GameSessionContext gameSessionContext);

    /**
	 * Validate legal play.
	 *
	 * @param play the play
	 * @param room the room
	 */
    void validateLegalPlay(Play play, Room room);

    /**
	 * Validate cards in hand.
	 *
	 * @param cards    the cards
	 * @param room     the room
	 * @param playerId the player id
	 */
    void validateCardsInHand(List<Card> cards, Room room, String playerId);
}
