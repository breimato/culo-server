package com.breixo.culo.domain.model;

import com.breixo.culo.domain.PlayerRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class Player {

    @NotBlank
    private final String id;

    @NotBlank
    private final String clientId;

    @NotBlank
    private final String nick;

    @Setter
    @Builder.Default
    private boolean connected = true;

    @Setter
    @NotNull
    @Builder.Default
    private PlayerRole role = PlayerRole.NONE;
}
