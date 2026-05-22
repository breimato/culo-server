package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.CardRank;
import lombok.Builder;

import java.util.List;
import java.util.Set;

/** The Record Round. */
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

    public Round {
        playersPassedSinceLastPlay = Set.copyOf(playersPassedSinceLastPlay);
        lastPlayedCards = List.copyOf(lastPlayedCards);
    }
}
