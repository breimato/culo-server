package com.breixo.culo.domain.port.input.round;

import com.breixo.culo.domain.model.play.Play;
import com.breixo.culo.domain.model.play.Round;

/** The Interface RoundService. */
public interface RoundService {

    /**
     * Register play.
     *
     * @param round    the round
     * @param play     the play
     * @param playerId the player id
     * @return the round
     */
    Round registerPlay(Round round, Play play, String playerId);

    /**
     * Register pass.
     *
     * @param round    the round
     * @param playerId the player id
     * @return the round
     */
    Round registerPass(Round round, String playerId);

    /**
     * Register plin play.
     *
     * @param round           the round
     * @param play            the play
     * @param playerId        the player id
     * @param skippedPlayerId the skipped player id
     * @return the round
     */
    Round registerPlinPlay(Round round, Play play, String playerId, String skippedPlayerId);

    /**
     * Reset.
     *
     * @param round the round
     * @return the round
     */
    Round reset(Round round);
}
