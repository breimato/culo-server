package com.breixo.culo.domain.model.room;

import com.breixo.culo.domain.model.Room;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RoomJoinResult(
        @NotBlank String roomCode,
        @NotBlank String playerId,
        @NotNull Room room
) {
}
