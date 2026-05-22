package com.breixo.culo.domain.command.room;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * The Record CloseRoomCommand.
 *
 * @param clientId the client id
 * @param roomCode the room code
 */
@Builder
public record CloseRoomCommand(
        @NotBlank String clientId,
        @NotBlank String roomCode
) {
}
