package com.breixo.culo.domain.model.play;

import com.breixo.culo.domain.model.card.Card;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

/** The Record Play. */
@Builder(toBuilder = true)
public record Play(
        @NotNull @NotEmpty List<Card> cards
) {

    public Play {
        cards = List.copyOf(cards);
    }
}
