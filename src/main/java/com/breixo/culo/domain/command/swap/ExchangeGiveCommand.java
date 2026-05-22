package com.breixo.culo.domain.command.swap;

import com.breixo.culo.domain.command.cards.CardInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

/**
 * The Record ExchangeGiveCommand.
 *
 * @param clientId the client id
 * @param roomCode the room code
 * @param cards    the cards
 */
@Builder
public record ExchangeGiveCommand(
        @NotBlank String clientId,
        @NotBlank String roomCode,
        @NotEmpty List<CardInput> cards
) {
}
