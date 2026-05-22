package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.room.Room;
import lombok.Builder;

/**
 * The Record PlayResult.
 *
 * @param room         the room
 * @param playerId     the player id
 * @param play         the play
 * @param plin         the plin
 * @param roundClosed  the round closed
 * @param gameFinished the game finished
 */
@Builder
public record PlayResult(
        Room room,
        String playerId,
        Play play,
        boolean plin,
        boolean roundClosed,
        boolean gameFinished
) {
}
