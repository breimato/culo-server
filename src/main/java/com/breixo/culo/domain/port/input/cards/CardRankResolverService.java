package com.breixo.culo.domain.port.input.cards;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.CardRank;

/**
 * The Interface CardRankResolverService.
 */
public interface CardRankResolverService {

    /**
	 * Resolve.
	 *
	 * @param card the card
	 * @return the card rank
	 */
    CardRank resolve(Card card);
}
