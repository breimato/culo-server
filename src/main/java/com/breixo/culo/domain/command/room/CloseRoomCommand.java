package com.breixo.culo.domain.command.room;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/** Close room command (host only). */
@Builder
public record CloseRoomCommand(
        @NotBlank String clientId,
        @NotBlank String roomCode
) {
}
