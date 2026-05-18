package com.breixo.culo.domain.command.room;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record JoinRoomCommand(
        @NotBlank String clientId,
        @NotBlank String roomCode,
        @NotBlank String nick
) {
}
