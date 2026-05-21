package com.breixo.culo.domain.model.room;

import com.breixo.culo.domain.model.room.enums.GamePhase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

/**
 * The Record RoomLobby.
 */
@Builder(toBuilder = true)
public record RoomLobby(
        @NotBlank String code,
        @NotBlank String hostPlayerId,
        @NotNull List<Player> players,
        @NotNull GamePhase phase,
        @NotNull Instant lastActivity
) {

    public RoomLobby {
        players = List.copyOf(players);
    }
}
