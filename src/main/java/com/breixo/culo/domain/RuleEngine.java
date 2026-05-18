package com.breixo.culo.domain;

import com.breixo.culo.domain.model.Play;
import com.breixo.culo.domain.model.Round;

import java.util.List;

/**
 * Motor de reglas puro del juego. Sin dependencias de framework.
 */
public class RuleEngine {

    /**
     * Determina si una jugada es legal dado el estado actual de la ronda.
     *
     * @param play  jugada que el jugador quiere realizar
     * @param round estado actual de la ronda
     * @return true si la jugada es legal
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
     * Detecta si la jugada es un "plin" (mismo número que la jugada anterior).
     *
     * @param play  jugada actual
     * @param round estado actual de la ronda
     * @return true si es plin
     */
    public boolean isPlin(final Play play, final Round round) {
        if (round.isOpen()) {
            return false;
        }
        return play.cardNumber() == round.getLastCardNumber();
    }

    /**
     * Determina si la ronda ha terminado: todos los jugadores activos excepto
     * quien hizo la última jugada han pasado al menos una vez.
     *
     * @param round            estado de la ronda
     * @param activePlayerIds  ids de jugadores con cartas en mano
     * @return true si la ronda debe cerrarse
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
