package com.breixo.culo.domain.command.room;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * The Record JoinRoomCommand.
 *
 * @param clientId the client id
 * @param roomCode the room code
 * @param nick     the nick
 */
@Builder
public record JoinRoomCommand(
        @NotBlank String clientId,
        @NotBlank String roomCode,
        @NotBlank String nick
) {
}
