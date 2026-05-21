package com.breixo.culo.domain.port.input.session;

import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.model.session.GameSessionContext;

import java.util.List;

/** The Interface GameSessionContextService. */
public interface GameSessionContextService {

    /**
     * Load.
     *
     * @param roomCode the room code
     * @param clientId the client id
     * @return the game session context
     */
    GameSessionContext load(String roomCode, String clientId);

    /**
     * Load with phase.
     *
     * @param roomCode       the room code
     * @param clientId       the client id
     * @param expectedPhase  the expected phase
     * @return the game session context
     */
    GameSessionContext loadWithPhase(String roomCode, String clientId, GamePhase expectedPhase);

    /**
     * Current player id.
     *
     * @param room the room
     * @return the string
     */
    String currentPlayerId(Room room);

    /**
     * Checks if is player out.
     *
     * @param room     the room
     * @param playerId the player id
     * @return true, if is player out
     */
    boolean isPlayerOut(Room room, String playerId);

    /**
     * Active player ids.
     *
     * @param room the room
     * @return the list
     */
    List<String> activePlayerIds(Room room);

    /**
     * Require player turn.
     *
     * @param room   the room
     * @param player the player
     */
    void requirePlayerTurn(Room room, Player player);

    /**
     * Require player has cards.
     *
     * @param room   the room
     * @param player the player
     */
    void requirePlayerHasCards(Room room, Player player);
}
