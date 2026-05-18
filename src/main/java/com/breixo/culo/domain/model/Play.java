package com.breixo.culo.domain.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

/**
 * Jugada: 1, 2 o 3 cartas del mismo número.
 */
@Builder
public record Play(
        @NotNull @NotEmpty List<Card> cards
) {

    public Play {
        if (cards.isEmpty() || cards.size() > 3) {
            throw new IllegalArgumentException("Una jugada debe tener entre 1 y 3 cartas");
        }
        final var firstNumber = cards.getFirst().number();
        final var allSameNumber = cards.stream().allMatch(card -> card.number() == firstNumber);
        if (!allSameNumber) {
            throw new IllegalArgumentException("Todas las cartas de una jugada deben tener el mismo número");
        }
    }

    public int size() {
        return this.cards.size();
    }

    public CardRank rank() {
        return CardRank.of(this.cards.getFirst());
    }

    public int cardNumber() {
        return this.cards.getFirst().number();
    }

    public boolean isAsOros() {
        return this.size() == 1
                && this.cards.getFirst().number() == 1
                && this.cards.getFirst().suit() == Suit.OROS;
    }
}
