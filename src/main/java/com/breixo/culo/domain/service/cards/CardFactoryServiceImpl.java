package com.breixo.culo.domain.service.cards;

import com.breixo.culo.domain.constants.DeckConstants;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.Suit;
import com.breixo.culo.domain.port.input.cards.CardFactoryService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

/** The Class CardFactoryServiceImpl. */
@Service
public class CardFactoryServiceImpl implements CardFactoryService {

    /** {@inheritDoc} */
    @Override
    public Card buildCard(final Suit suit, final Integer number) {

        if (BooleanUtils.isFalse(DeckConstants.DECK_NUMBERS.contains(number))) {
            throw new GameException(GameExceptionConstants.INVALID_CARD_NUMBER);
        }

        return Card.builder()
                .suit(suit)
                .number(number)
                .build();
    }
}
