package com.breixo.culo.domain.service.play;

import com.breixo.culo.domain.command.game.CardInput;
import com.breixo.culo.domain.constants.GameConstants;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.play.Play;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.card.CardFactoryService;
import com.breixo.culo.domain.port.input.play.PlayBuilderService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/** The Class PlayBuilderServiceImpl. */
@Service
@RequiredArgsConstructor
public class PlayBuilderServiceImpl implements PlayBuilderService {

    /** The card factory service. */
    private final CardFactoryService cardFactoryService;

    /** {@inheritDoc} */
    @Override
    public Play buildPlay(final List<Card> cards) {

        if (CollectionUtils.isEmpty(cards) || cards.size() > GameConstants.MAX_CARDS_PER_PLAY) {
            throw new GameException(GameExceptionConstants.INVALID_PLAY);
        }

        final var firstNumber = cards.getFirst().number();
        final var allSameNumber = cards.stream()
                .allMatch(card -> card.number().equals(firstNumber));

        if (BooleanUtils.isFalse(allSameNumber)) {
            throw new GameException(GameExceptionConstants.INVALID_PLAY);
        }

        return Play.builder()
                .cards(cards)
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public List<Card> toCards(final List<CardInput> cardInputs, final Room room, final String playerId) {

        final var hand = room.gameSession().hands().getOrDefault(playerId, List.of());
        final var cards = cardInputs.stream()
                .map(cardInput -> this.cardFactoryService.buildCard(cardInput.suit(), cardInput.number()))
                .toList();

        if (BooleanUtils.isFalse(hand.containsAll(cards))) {
            throw new GameException(GameExceptionConstants.CARDS_NOT_IN_HAND);
        }

        return cards;
    }
}
