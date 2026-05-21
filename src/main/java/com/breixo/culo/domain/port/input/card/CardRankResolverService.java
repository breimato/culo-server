package com.breixo.culo.domain.port.input.card;

import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.card.enums.CardRank;

/** The Interface CardRankResolverService. */
public interface CardRankResolverService {

    /**
     * Resolve.
     *
     * @param card the card
     * @return the card rank
     */
    CardRank resolve(Card card);
}
