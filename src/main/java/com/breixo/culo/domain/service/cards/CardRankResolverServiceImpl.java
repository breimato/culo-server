package com.breixo.culo.domain.service.cards;

import com.breixo.culo.domain.constants.DeckConstants;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.CardRank;
import com.breixo.culo.domain.model.cards.enums.Suit;
import com.breixo.culo.domain.port.input.cards.CardRankResolverService;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

/** The Class CardRankResolverServiceImpl. */
@Service
public class CardRankResolverServiceImpl implements CardRankResolverService {

    /** The Constant AS_NUMBER. */
    private static final Integer AS_NUMBER = 1;

    /** {@inheritDoc} */
    @Override
    public CardRank resolve(final Card card) {

        final var number = card.number();

        if (BooleanUtils.isFalse(DeckConstants.DECK_NUMBERS.contains(number))) {
            throw new GameException(GameExceptionConstants.INVALID_CARD_NUMBER);
        }

        if (AS_NUMBER.equals(number) && Suit.OROS.equals(card.suit())) {
            return CardRank.AS_OROS;
        }

        if (AS_NUMBER.equals(number)) {
            return CardRank.AS_OTRO;
        }

        return this.rankForNumber(number);
    }

    private CardRank rankForNumber(final Integer number) {

        return switch (number) {
            case 2 -> CardRank.DOS;
            case 3 -> CardRank.TRES;
            case 4 -> CardRank.CUATRO;
            case 5 -> CardRank.CINCO;
            case 6 -> CardRank.SEIS;
            case 7 -> CardRank.SIETE;
            case 10 -> CardRank.SOTA;
            case 11 -> CardRank.CABALLO;
            case 12 -> CardRank.REY;
            default -> throw new GameException(GameExceptionConstants.INVALID_CARD_NUMBER);
        };
    }
}
