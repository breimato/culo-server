package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.game.Round;

import java.util.List;

/** The Interface PlayRuleService. */
public interface PlayRuleService {

    /**
     * Checks if is legal.
     *
     * @param play  the play
     * @param round the round
     * @return true, if is legal
     */
    boolean isLegal(Play play, Round round);

    /**
     * Checks if is plin.
     *
     * @param play  the play
     * @param round the round
     * @return true, if is plin
     */
    boolean isPlin(Play play, Round round);

    /**
     * Checks if is round over.
     *
     * @param round           the round
     * @param activePlayerIds the active player ids
     * @return true, if is round over
     */
    boolean isRoundOver(Round round, List<String> activePlayerIds);

    /**
     * Checks if is as oros.
     *
     * @param play the play
     * @return true, if is as oros
     */
    boolean isAsOros(Play play);
}
