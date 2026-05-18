package com.breixo.culo.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Estado de una ronda dentro de la partida.
 * Una ronda termina cuando todos los demás jugadores pasan en cadena.
 */
@Getter
public class Round {

    /** Cantidad de cartas obligatoria (0 = aún no fijada, primer turno de la ronda). */
    @Setter
    private int requirement = 0;

    /** Rango de la última jugada (null = ronda recién abierta). */
    @Setter
    private CardRank lastRank = null;

    /** Número exacto de la última jugada (para detectar plin). */
    @Setter
    private int lastCardNumber = 0;

    /** ID del jugador que realizó la última jugada. */
    @Setter
    private String lastPlayerId = null;

    /**
     * Jugador saltado por el último plin (el siguiente en turno; no debe pasar en esta ronda).
     */
    @Setter
    private String skippedPlayerId = null;

    /** Jugadores que han pasado desde la última jugada (excluye al que debe abrir). */
    private final Set<String> playersPassedSinceLastPlay = new HashSet<>();

    /** Cartas de la última jugada (vacío tras reset). */
    private List<Card> lastPlayedCards = new ArrayList<>();

    public void registerPlay(final Play play, final String playerId) {
        this.skippedPlayerId = null;
        this.requirement = play.size();
        this.lastRank = play.rank();
        this.lastCardNumber = play.cardNumber();
        this.lastPlayerId = playerId;
        this.playersPassedSinceLastPlay.clear();
        this.lastPlayedCards = new ArrayList<>(play.cards());
    }

    public void registerPass(final String playerId) {
        this.playersPassedSinceLastPlay.add(playerId);
    }

    public void registerPlinPlay(final Play play, final String playerId, final String skippedPlayerId) {
        this.registerPlay(play, playerId);
        this.skippedPlayerId = skippedPlayerId;
    }

    public void reset() {
        this.requirement = 0;
        this.lastRank = null;
        this.lastCardNumber = 0;
        this.lastPlayerId = null;
        this.skippedPlayerId = null;
        this.playersPassedSinceLastPlay.clear();
        this.lastPlayedCards = new ArrayList<>();
    }

    public boolean isOpen() {
        return this.lastRank == null;
    }
}
