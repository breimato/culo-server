package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.model.cards.enums.Suit;
import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.game.Round;
import com.breixo.culo.domain.port.input.cards.CardRankResolverService;
import com.breixo.culo.domain.port.input.game.PlayRuleService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/** The Class PlayRuleServiceImpl. */
@Service
@RequiredArgsConstructor
public class PlayRuleServiceImpl implements PlayRuleService {

    /** The card rank resolver service. */
    private final CardRankResolverService cardRankResolverService;

    /** {@inheritDoc} */
    @Override
    public boolean isLegal(final Play play, final Round round) {

        final var leadCard = play.cards().getFirst();

        if (this.isAsOros(play)) {
            return true;
        }

        if (Objects.isNull(round.lastRank())) {
            return true;
        }

        if (BooleanUtils.isFalse(Integer.valueOf(play.cards().size()).equals(round.requirement()))) {
            return false;
        }

        final var playRank = this.cardRankResolverService.resolve(leadCard);

        return Integer.compare(playRank.getPower(), round.lastRank().getPower()) >= 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPlin(final Play play, final Round round) {

        if (Objects.isNull(round.lastRank())) {
            return false;
        }

        return play.cards().getFirst().number().equals(round.lastCardNumber());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRoundOver(final Round round, final List<String> activePlayerIds) {

        if (Objects.isNull(round.lastRank())) {
            return false;
        }

        final var lastPlayerId = round.lastPlayerId();
        final var skippedPlayerId = round.skippedPlayerId();
        final var othersMustPass = activePlayerIds.stream()
                .filter(playerId -> BooleanUtils.isFalse(playerId.equals(lastPlayerId)))
                .filter(playerId -> Objects.isNull(skippedPlayerId) || BooleanUtils.isFalse(playerId.equals(skippedPlayerId)))
                .count();

        if (Long.valueOf(0L).equals(othersMustPass)) {
            return Objects.nonNull(skippedPlayerId);
        }

        final var othersWhoPassed = activePlayerIds.stream()
                .filter(playerId -> BooleanUtils.isFalse(playerId.equals(lastPlayerId)))
                .filter(playerId -> Objects.isNull(skippedPlayerId) || BooleanUtils.isFalse(playerId.equals(skippedPlayerId)))
                .filter(playerId -> round.playersPassedSinceLastPlay().contains(playerId))
                .count();

        return othersWhoPassed >= othersMustPass;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAsOros(final Play play) {

        final var leadCard = play.cards().getFirst();
        return Integer.valueOf(1).equals(play.cards().size())
                && Integer.valueOf(1).equals(leadCard.number())
                && Suit.OROS.equals(leadCard.suit());
    }
}
