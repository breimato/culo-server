package com.breixo.culo.domain.model.room;

import com.breixo.culo.domain.model.cards.Card;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.Set;

/**
 * The Record ExchangeState.
 *
 * @param pendingGanadorToCulo         the pending ganador to culo
 * @param pendingSubcampeonToPenultimo the pending subcampeon to penultimo
 * @param exchangeDone                 the exchange done
 */
@Builder(toBuilder = true)
public record ExchangeState(
        @NotNull List<Card> pendingGanadorToCulo,
        @NotNull List<Card> pendingSubcampeonToPenultimo,
        @NotNull Set<String> exchangeDone
) {

    /**
	 * Instantiates a new exchange state.
	 *
	 * @param pendingGanadorToCulo         the pending ganador to culo
	 * @param pendingSubcampeonToPenultimo the pending subcampeon to penultimo
	 * @param exchangeDone                 the exchange done
	 */
    public ExchangeState {
        pendingGanadorToCulo = List.copyOf(pendingGanadorToCulo);
        pendingSubcampeonToPenultimo = List.copyOf(pendingSubcampeonToPenultimo);
        exchangeDone = Set.copyOf(exchangeDone);
    }
}
