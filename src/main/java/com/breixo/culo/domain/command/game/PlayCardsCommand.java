package com.breixo.culo.domain.command.game;

import com.breixo.culo.domain.command.cards.CardInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record PlayCardsCommand(
        @NotBlank String clientId,
        @NotBlank String roomCode,
        @NotEmpty List<CardInput> cards
) {
}
