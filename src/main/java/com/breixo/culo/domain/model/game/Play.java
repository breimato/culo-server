package com.breixo.culo.domain.model.game;

import com.breixo.culo.domain.model.cards.Card;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

/**
 * The Record Play.
 *
 * @param cards the cards
 */
@Builder(toBuilder = true)
public record Play(
        @NotNull @NotEmpty List<Card> cards
) {

    /**
	 * Instantiates a new play.
	 *
	 * @param cards the cards
	 */
    public Play {
        cards = List.copyOf(cards);
    }
}
