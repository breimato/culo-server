package com.breixo.culo.domain;

import com.breixo.culo.domain.model.Play;
import com.breixo.culo.domain.model.Round;

import java.util.List;

/**
 * The Class RuleEngine.
 */
public class RuleEngine {

    /**
	 * Checks if is legal.
	 *
	 * @param play  the play
	 * @param round the round
	 * @return true, if is legal
	 */
    public boolean isLegal(final Play play, final Round round) {
        if (play.isAsOros()) {
            return true;
        }
        if (round.isOpen()) {
            return true;
        }
        if (play.size() != round.getRequirement()) {
            return false;
        }
        return play.rank().isHigherOrEqualThan(round.getLastRank());
    }

    /**
	 * Checks if is plin.
	 *
	 * @param play  the play
	 * @param round the round
	 * @return true, if is plin
	 */
    public boolean isPlin(final Play play, final Round round) {
        if (round.isOpen()) {
            return false;
        }
        return play.cardNumber() == round.getLastCardNumber();
    }

    /**
	 * Checks if is round over.
	 *
	 * @param round           the round
	 * @param activePlayerIds the active player ids
	 * @return true, if is round over
	 */
    public boolean isRoundOver(final Round round, final List<String> activePlayerIds) {
        if (round.isOpen()) {
            return false;
        }
        final var lastPlayerId = round.getLastPlayerId();
        final var skippedPlayerId = round.getSkippedPlayerId();
        final var othersMustPass = activePlayerIds.stream()
                .filter(id -> !id.equals(lastPlayerId))
                .filter(id -> skippedPlayerId == null || !id.equals(skippedPlayerId))
                .count();
        if (othersMustPass == 0) {
            return skippedPlayerId != null;
        }
        final var othersWhoPassed = activePlayerIds.stream()
                .filter(id -> !id.equals(lastPlayerId))
                .filter(id -> skippedPlayerId == null || !id.equals(skippedPlayerId))
                .filter(id -> round.getPlayersPassedSinceLastPlay().contains(id))
                .count();
        return othersWhoPassed >= othersMustPass;
    }
}
