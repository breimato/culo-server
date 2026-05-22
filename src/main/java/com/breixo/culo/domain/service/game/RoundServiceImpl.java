package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.game.Round;
import com.breixo.culo.domain.port.input.cards.CardRankResolverService;
import com.breixo.culo.domain.port.input.game.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

/**
 * The Class RoundServiceImpl.
 */
@Service
@RequiredArgsConstructor
public class RoundServiceImpl implements RoundService {

    /** The card rank resolver service. */
    private final CardRankResolverService cardRankResolverService;

    /** {@inheritDoc} */
    @Override
    public Round registerPlay(final Round round, final Play play, final String playerId) {

        final var leadCard = play.cards().getFirst();

        return round.toBuilder()
                .skippedPlayerId(null)
                .requirement(play.cards().size())
                .lastRank(this.cardRankResolverService.resolve(leadCard))
                .lastCardNumber(leadCard.number())
                .lastPlayerId(playerId)
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(play.cards())
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public Round registerPass(final Round round, final String playerId) {

        final var playersPassedSinceLastPlay = new HashSet<>(round.playersPassedSinceLastPlay());
        playersPassedSinceLastPlay.add(playerId);

        return round.toBuilder()
                .playersPassedSinceLastPlay(playersPassedSinceLastPlay)
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public Round registerPlinPlay(
            final Round round,
            final Play play,
            final String playerId,
            final String skippedPlayerId) {

        final var updatedRound = this.registerPlay(round, play, playerId);

        return updatedRound.toBuilder()
                .skippedPlayerId(skippedPlayerId)
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public Round reset(final Round round) {

        return Round.builder()
                .requirement(0)
                .lastRank(null)
                .lastCardNumber(0)
                .lastPlayerId(null)
                .skippedPlayerId(null)
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of())
                .build();
    }
}
