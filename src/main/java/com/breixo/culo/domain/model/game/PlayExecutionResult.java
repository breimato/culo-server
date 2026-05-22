package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/**
 * The Record PlayExecutionResult.
 *
 * @param room         the room
 * @param plin         the plin
 * @param roundClosed  the round closed
 * @param gameFinished the game finished
 */
@Builder
public record PlayExecutionResult(
        Room room,
        boolean plin,
        boolean roundClosed,
        boolean gameFinished
) {
}
