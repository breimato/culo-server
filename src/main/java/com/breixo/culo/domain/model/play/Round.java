package com.breixo.culo.domain.model.play;

import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.card.enums.CardRank;
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
