package com.breixo.culo.testsupport;

import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.play.Round;
import com.breixo.culo.domain.model.room.CuloSwapState;
import com.breixo.culo.domain.model.room.ExchangeState;
import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.model.room.enums.PlayerRole;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Class RoomTestFactory.
 */
public final class RoomTestFactory {

    private RoomTestFactory() {
    }

    /**
     * Empty room.
     *
     * @param roomCode     the room code
     * @param hostPlayerId the host player id
     * @return the room
     */
    public static Room emptyRoom(final String roomCode, final String hostPlayerId) {
        return Room.builder()
                .roomLobby(RoomLobby.builder()
                        .code(roomCode)
                        .hostPlayerId(hostPlayerId)
                        .players(List.of())
                        .phase(GamePhase.LOBBY)
                        .lastActivity(Instant.now())
                        .build())
                .gameSession(emptyGameSession())
                .exchangeState(emptyExchangeState())
                .culoSwapState(emptyCuloSwapState())
                .build();
    }

    /**
     * Room with players.
     *
     * @param roomCode     the room code
     * @param hostPlayerId the host player id
     * @param players      the players
     * @return the room
     */
    public static Room roomWithPlayers(
            final String roomCode,
            final String hostPlayerId,
            final List<Player> players) {
        return emptyRoom(roomCode, hostPlayerId).toBuilder()
                .roomLobby(emptyRoom(roomCode, hostPlayerId).roomLobby().toBuilder()
                        .players(players)
                        .build())
                .build();
    }

    /**
     * Mutable game session builder state for tests.
     *
     * @return the game session
     */
    public static GameSession emptyGameSession() {
        return GameSession.builder()
                .hands(new HashMap<>())
                .playerOrder(new ArrayList<>())
                .currentPlayerIndex(0)
                .currentRound(emptyRound())
                .playEpoch(0)
                .finishOrder(new ArrayList<>())
                .pendingQuadDiscards(new ArrayList<>())
                .build();
    }

    /**
     * Empty round.
     *
     * @return the round
     */
    public static Round emptyRound() {
        return Round.builder()
                .requirement(0)
                .lastCardNumber(0)
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(new ArrayList<>())
                .build();
    }

    /**
     * Empty exchange state.
     *
     * @return the exchange state
     */
    public static ExchangeState emptyExchangeState() {
        return ExchangeState.builder()
                .pendingGanadorToCulo(new ArrayList<>())
                .pendingSubcampeonToPenultimo(new ArrayList<>())
                .exchangeDone(new HashSet<>())
                .build();
    }

    /**
     * Empty culo swap state.
     *
     * @return the culo swap state
     */
    public static CuloSwapState emptyCuloSwapState() {
        return CuloSwapState.builder()
                .votes(new HashMap<>())
                .build();
    }

    /**
     * Player builder with defaults.
     *
     * @param id       the id
     * @param clientId the client id
     * @param nick     the nick
     * @return the player
     */
    public static Player player(final String id, final String clientId, final String nick) {
        return Player.builder()
                .id(id)
                .clientId(clientId)
                .nick(nick)
                .connected(true)
                .role(PlayerRole.NONE)
                .build();
    }

    /**
     * Room with custom game session.
     *
     * @param room         the room
     * @param gameSession  the game session
     * @return the room
     */
    public static Room withGameSession(final Room room, final GameSession gameSession) {
        return room.toBuilder().gameSession(gameSession).build();
    }

    /**
     * Room with hands.
     *
     * @param room  the room
     * @param hands the hands
     * @return the room
     */
    public static Room withHands(final Room room, final Map<String, List<Card>> hands) {
        return room.toBuilder()
                .gameSession(room.gameSession().toBuilder().hands(hands).build())
                .build();
    }

    /**
     * Room with phase.
     *
     * @param room  the room
     * @param phase the phase
     * @return the room
     */
    public static Room withPhase(final Room room, final GamePhase phase) {
        return room.toBuilder()
                .roomLobby(room.roomLobby().toBuilder().phase(phase).build())
                .build();
    }

    /**
     * Room with player order.
     *
     * @param room        the room
     * @param playerOrder the player order
     * @return the room
     */
    public static Room withPlayerOrder(final Room room, final List<String> playerOrder) {
        return room.toBuilder()
                .gameSession(room.gameSession().toBuilder().playerOrder(playerOrder).build())
                .build();
    }

    /**
     * Room with current player index.
     *
     * @param room                the room
     * @param currentPlayerIndex  the current player index
     * @return the room
     */
    public static Room withCurrentPlayerIndex(final Room room, final Integer currentPlayerIndex) {
        return room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .currentPlayerIndex(currentPlayerIndex)
                        .build())
                .build();
    }

    /**
     * Room with current round.
     *
     * @param room  the room
     * @param round the round
     * @return the room
     */
    public static Room withCurrentRound(final Room room, final Round round) {
        return room.toBuilder()
                .gameSession(room.gameSession().toBuilder().currentRound(round).build())
                .build();
    }

    /**
     * Room with culo swap state fields.
     *
     * @param room        the room
     * @param initiatorId the initiator id
     * @param targetId    the target id
     * @param votes       the votes
     * @return the room
     */
    public static Room withCuloSwapState(
            final Room room,
            final String initiatorId,
            final String targetId,
            final Map<String, Boolean> votes) {
        return room.toBuilder()
                .culoSwapState(room.culoSwapState().toBuilder()
                        .initiatorId(initiatorId)
                        .targetId(targetId)
                        .votes(votes)
                        .build())
                .build();
    }

    /**
     * Room with exchange done.
     *
     * @param room         the room
     * @param exchangeDone the exchange done
     * @return the room
     */
    public static Room withExchangeDone(final Room room, final Set<String> exchangeDone) {
        return room.toBuilder()
                .exchangeState(room.exchangeState().toBuilder().exchangeDone(exchangeDone).build())
                .build();
    }
}
