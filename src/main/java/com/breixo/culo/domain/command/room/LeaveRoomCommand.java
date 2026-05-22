package com.breixo.culo.domain.command.room;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * The Record LeaveRoomCommand.
 *
 * @param clientId the client id
 * @param roomCode the room code
 */
@Builder
public record LeaveRoomCommand(
        @NotBlank String clientId,
        @NotBlank String roomCode
) {
}
