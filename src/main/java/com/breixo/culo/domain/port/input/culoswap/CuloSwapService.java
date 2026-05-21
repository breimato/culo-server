package com.breixo.culo.domain.port.input.culoswap;

import com.breixo.culo.domain.model.culoswap.CuloSwapVoteRegistrationResult;
import com.breixo.culo.domain.model.room.Room;

/** The Interface CuloSwapService. */
public interface CuloSwapService {

    /**
     * Register culo swap vote.
     *
     * @param room     the room
     * @param playerId the player id
     * @param accept   the accept
     * @return the culo swap vote registration result
     */
    CuloSwapVoteRegistrationResult registerCuloSwapVote(Room room, String playerId, boolean accept);

    /**
     * Checks if is culo swap approved.
     *
     * @param room the room
     * @return true, if is culo swap approved
     */
    boolean isCuloSwapApproved(Room room);

    /**
     * Apply culo swap.
     *
     * @param room the room
     * @return the room
     */
    Room applyCuloSwap(Room room);

    /**
     * Clear culo swap.
     *
     * @param room the room
     * @return the room
     */
    Room clearCuloSwap(Room room);

    /**
     * Initiate swap.
     *
     * @param room           the room
     * @param initiatorId    the initiator id
     * @param targetPlayerId the target player id
     * @return the room
     */
    Room initiateSwap(Room room, String initiatorId, String targetPlayerId);
}
