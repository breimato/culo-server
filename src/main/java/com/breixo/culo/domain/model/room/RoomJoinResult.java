package com.breixo.culo.domain.model.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * The Record RoomJoinResult.
 *
 * @param roomCode the room code
 * @param playerId the player id
 * @param room     the room
 */
@Builder
public record RoomJoinResult(
        @NotBlank String roomCode,
        @NotBlank String playerId,
        @NotNull Room room
) {
}
