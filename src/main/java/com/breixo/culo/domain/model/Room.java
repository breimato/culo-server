package com.breixo.culo.domain.model;

import com.breixo.culo.domain.GamePhase;
import com.breixo.culo.domain.PlayerRole;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class Room {

    private static final int MAX_PLAYERS = 10;
    private static final Set<Integer> DECK_NUMBERS = Set.of(1, 2, 3, 4, 5, 6, 7, 10, 11, 12);

    @NotBlank
    private final String code;

    @NotBlank
    private final String hostPlayerId;

    private final List<Player> players = new ArrayList<>();

    @Setter
    @NotNull
    private GamePhase phase = GamePhase.LOBBY;

    @Setter
    private Instant lastActivity = Instant.now();

    // ─── Game state ───────────────────────────────────────────────────────────

    /** Manos privadas: playerId → cartas. */
    private final Map<String, List<Card>> hands = new HashMap<>();

    /** Orden de turno (IDs de jugadores). */
    private final List<String> playerOrder = new ArrayList<>();

    /** Índice del jugador en turno actual. */
    @Setter
    private int currentPlayerIndex = 0;

    /** Estado de la ronda actual. */
    private Round currentRound = new Round();

    /** ID del culo de la partida anterior (null = primera partida). */
    @Setter
    private String lastCuloId = null;

    /** Orden en que los jugadores se han quedado sin cartas (1º = ganador). */
    private final List<String> finishOrder = new ArrayList<>();

    // ─── Exchange state ───────────────────────────────────────────────────────

    /** Cartas que el ganador aún no ha devuelto al culo. */
    private final List<Card> pendingExchangeGanadorToCulo = new ArrayList<>();

    /** Cartas que el subcampeón aún no ha devuelto al penúltimo. */
    private final List<Card> pendingExchangeSubcampeonToPenultimo = new ArrayList<>();

    /** Jugadores que han completado su parte del intercambio. */
    private final java.util.Set<String> exchangeDone = new java.util.HashSet<>();

    // ─── CuloSwap state ───────────────────────────────────────────────────────

    @Setter
    private String culoSwapInitiatorId = null;

    @Setter
    private String culoSwapTargetId = null;

    private final Map<String, Boolean> culoSwapVotes = new HashMap<>();

    // ─── Room management ─────────────────────────────────────────────────────

    public void addPlayer(@NotNull final Player player) {
        if (this.players.size() >= MAX_PLAYERS) {
            throw new RoomException(RoomExceptionConstants.ROOM_FULL);
        }
        this.players.add(player);
        this.touch();
    }

    public Optional<Player> findPlayerByClientId(@NotBlank final String clientId) {
        return this.players.stream()
                .filter(player -> player.getClientId().equals(clientId))
                .findFirst();
    }

    public Optional<Player> findPlayerById(@NotBlank final String playerId) {
        return this.players.stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst();
    }

    public boolean isHost(@NotBlank final String playerId) {
        return this.hostPlayerId.equals(playerId);
    }

    public boolean isFull() {
        return this.players.size() >= MAX_PLAYERS;
    }

    public void touch() {
        this.lastActivity = Instant.now();
    }

    // ─── Game logic ───────────────────────────────────────────────────────────

    /**
     * Reparte el mazo a todos los jugadores y pasa la fase a PLAYING.
     * Si hay culo previo, empieza él. Si es la primera partida, empieza quien tenga el 2 de oros.
     */
    public void dealCards() {
        final var deck = this.buildShuffledDeck();
        this.hands.clear();
        this.finishOrder.clear();
        this.exchangeDone.clear();
        this.pendingExchangeGanadorToCulo.clear();
        this.pendingExchangeSubcampeonToPenultimo.clear();

        this.playerOrder.clear();
        this.players.stream()
                .map(Player::getId)
                .forEach(this.playerOrder::add);

        final var dealerId = Optional.ofNullable(this.lastCuloId).orElse(this.hostPlayerId);
        final var dealerIdx = this.playerOrder.indexOf(dealerId);
        if (dealerIdx > 0) {
            Collections.rotate(this.playerOrder, -dealerIdx);
        }

        this.playerOrder.forEach(playerId -> this.hands.put(playerId, new ArrayList<>()));

        final int n = this.playerOrder.size();
        for (int i = 0; i < deck.size(); i++) {
            this.hands.get(this.playerOrder.get(i % n)).add(deck.get(i));
        }

        this.currentRound = new Round();
        this.currentPlayerIndex = this.lastCuloId == null
                ? this.findTwoOfOrosPlayerIndex()
                : 0;

        this.players.forEach(player -> player.setRole(PlayerRole.NONE));
    }

    private int findTwoOfOrosPlayerIndex() {
        final var twoOfOros = Card.builder().suit(Suit.OROS).number(2).build();
        for (int i = 0; i < this.playerOrder.size(); i++) {
            if (this.hands.get(this.playerOrder.get(i)).contains(twoOfOros)) {
                return i;
            }
        }
        return 0;
    }

    public String getCurrentPlayerId() {
        if (this.playerOrder.isEmpty()) {
            return null;
        }
        return this.playerOrder.get(this.currentPlayerIndex);
    }

    /** Siguiente jugador activo en el orden de turno (desde el jugador actual). */
    public String getNextActivePlayerId() {
        final int n = this.playerOrder.size();
        for (int step = 1; step <= n; step++) {
            final var nextIdx = (this.currentPlayerIndex + step) % n;
            final var playerId = this.playerOrder.get(nextIdx);
            if (!this.isPlayerOut(playerId)) {
                return playerId;
            }
        }
        return null;
    }

    /**
     * Avanza el turno al siguiente jugador activo (con cartas).
     *
     * @param skipOne si es true, salta un jugador adicional (plin)
     */
    public void advanceTurn(final boolean skipOne) {
        final int n = this.playerOrder.size();
        int steps = skipOne ? 2 : 1;
        for (int i = 0; i < steps; i++) {
            this.currentPlayerIndex = (this.currentPlayerIndex + 1) % n;
            int safety = 0;
            while (this.isPlayerOut(this.playerOrder.get(this.currentPlayerIndex)) && safety < n) {
                this.currentPlayerIndex = (this.currentPlayerIndex + 1) % n;
                safety++;
            }
        }
    }

    public List<Card> getHand(final String playerId) {
        return this.hands.getOrDefault(playerId, List.of());
    }

    public boolean isPlayerOut(final String playerId) {
        final var hand = this.hands.get(playerId);
        return hand == null || hand.isEmpty();
    }

    public int getActivePlayerCount() {
        return (int) this.playerOrder.stream()
                .filter(id -> !this.isPlayerOut(id))
                .count();
    }

    /**
     * Registra que un jugador se ha quedado sin cartas.
     * Asigna roles y detecta fin de partida.
     *
     * @param playerId ID del jugador que se ha quedado sin cartas
     * @return true si la partida ha terminado
     */
    public boolean registerPlayerOut(final String playerId) {
        this.finishOrder.add(playerId);
        final int active = this.getActivePlayerCount();
        if (active == 1) {
            final var culoId = this.playerOrder.stream()
                    .filter(id -> !this.isPlayerOut(id))
                    .findFirst()
                    .orElseThrow();
            this.finishOrder.add(culoId);
            this.assignRoles();
            return true;
        }
        return false;
    }

    private void assignRoles() {
        final int n = this.finishOrder.size();
        for (int i = 0; i < n; i++) {
            final var pid = this.finishOrder.get(i);
            final var player = this.findPlayerById(pid).orElseThrow();
            final PlayerRole role;
            if (i == 0) {
                role = PlayerRole.GANADOR;
            } else if (i == n - 1) {
                role = PlayerRole.CULO;
            } else if (i == n - 2) {
                role = PlayerRole.PENULTIMO;
            } else if (i == 1) {
                role = PlayerRole.SUBCAMPEON;
            } else {
                role = PlayerRole.NONE;
            }
            player.setRole(role);
        }
        this.lastCuloId = this.finishOrder.getLast();
    }

    public Optional<String> getPlayerIdByRole(final PlayerRole role) {
        return this.players.stream()
                .filter(player -> player.getRole() == role)
                .map(Player::getId)
                .findFirst();
    }

    /**
     * Registra el voto de un jugador en la votación de transferencia de culo.
     *
     * @return true si todos han votado
     */
    public boolean registerCuloSwapVote(final String playerId, final boolean accept) {
        this.culoSwapVotes.put(playerId, accept);
        return this.culoSwapVotes.size() == this.players.size();
    }

    public boolean isCuloSwapApproved() {
        if (this.culoSwapVotes.isEmpty()) {
            return false;
        }
        return this.culoSwapVotes.values().stream().allMatch(Boolean.TRUE::equals);
    }

    public void applyCuloSwap() {
        final var initiator = this.findPlayerById(this.culoSwapInitiatorId).orElseThrow();
        final var target = this.findPlayerById(this.culoSwapTargetId).orElseThrow();
        final var initiatorHand = new ArrayList<>(this.getHand(this.culoSwapInitiatorId));
        final var targetHand = new ArrayList<>(this.getHand(this.culoSwapTargetId));
        initiator.setRole(target.getRole());
        target.setRole(PlayerRole.CULO);
        this.hands.put(this.culoSwapInitiatorId, targetHand);
        this.hands.put(this.culoSwapTargetId, initiatorHand);
        this.lastCuloId = this.culoSwapTargetId;
    }

    public void clearCuloSwap() {
        this.culoSwapInitiatorId = null;
        this.culoSwapTargetId = null;
        this.culoSwapVotes.clear();
    }

    // ─── Deck ─────────────────────────────────────────────────────────────────

    private List<Card> buildShuffledDeck() {
        final var deck = Arrays.stream(Suit.values())
                .flatMap(suit -> DECK_NUMBERS.stream().map(number -> Card.builder().suit(suit).number(number).build()))
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(deck);
        return deck;
    }
}
