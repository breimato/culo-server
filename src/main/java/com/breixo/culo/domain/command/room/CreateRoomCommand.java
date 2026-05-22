package com.breixo.culo.domain.command.room;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * The Record CreateRoomCommand.
 *
 * @param clientId the client id
 * @param nick     the nick
 */
@Builder
public record CreateRoomCommand(
        @NotBlank String clientId,
        @NotBlank String nick
) {
}
