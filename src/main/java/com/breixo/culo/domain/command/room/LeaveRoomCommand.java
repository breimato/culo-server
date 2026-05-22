package com.breixo.culo.domain.command.room;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/** Leave room command. */
@Builder
public record LeaveRoomCommand(
        @NotBlank String clientId,
        @NotBlank String roomCode
) {
}
