package com.breixo.culo.domain.model.room;

import com.breixo.culo.domain.model.room.enums.PlayerRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * The Record Player.
 */
@Builder(toBuilder = true)
public record Player(
        @NotBlank String id,
        @NotBlank String clientId,
        @NotBlank String nick,
        boolean connected,
        @NotNull PlayerRole role
) {
}
