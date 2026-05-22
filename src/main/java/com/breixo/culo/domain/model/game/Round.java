package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.CardRank;
import lombok.Builder;

import java.util.List;
import java.util.Set;

/**
 * The Record Round.
 *
 * @param requirement                the requirement
 * @param lastRank                   the last rank
 * @param lastCardNumber             the last card number
 * @param lastPlayerId               the last player id
 * @param skippedPlayerId            the skipped player id
 * @param playersPassedSinceLastPlay the players passed since last play
 * @param lastPlayedCards            the last played cards
 */
@Builder(toBuilder = true)
public record Round(
        Integer requirement,
        CardRank lastRank,
        Integer lastCardNumber,
        String lastPlayerId,
        String skippedPlayerId,
        Set<String> playersPassedSinceLastPlay,
        List<Card> lastPlayedCards
) {

    /**
	 * Instantiates a new round.
	 *
	 * @param requirement                the requirement
	 * @param lastRank                   the last rank
	 * @param lastCardNumber             the last card number
	 * @param lastPlayerId               the last player id
	 * @param skippedPlayerId            the skipped player id
	 * @param playersPassedSinceLastPlay the players passed since last play
	 * @param lastPlayedCards            the last played cards
	 */
    public Round {
        playersPassedSinceLastPlay = Set.copyOf(playersPassedSinceLastPlay);
        lastPlayedCards = List.copyOf(lastPlayedCards);
    }
}
