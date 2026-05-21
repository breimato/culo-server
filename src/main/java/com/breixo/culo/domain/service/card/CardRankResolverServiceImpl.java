package com.breixo.culo.domain.service.card;

import com.breixo.culo.domain.constants.DeckConstants;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.card.enums.CardRank;
import com.breixo.culo.domain.model.card.enums.Suit;
import com.breixo.culo.domain.port.input.card.CardRankResolverService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

/** The Class CardRankResolverServiceImpl. */
@Service
public class CardRankResolverServiceImpl implements CardRankResolverService {

    /** {@inheritDoc} */
    @Override
    public CardRank resolve(final Card card) {

        if (BooleanUtils.isFalse(DeckConstants.DECK_NUMBERS.contains(card.number()))) {
            throw new GameException(GameExceptionConstants.INVALID_CARD_NUMBER);
        }

        if (Integer.valueOf(1).equals(card.number())) {
            if (Suit.OROS.equals(card.suit())) {
                return CardRank.AS_OROS;
            }
            return CardRank.AS_OTRO;
        }

        return switch (card.number()) {
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
