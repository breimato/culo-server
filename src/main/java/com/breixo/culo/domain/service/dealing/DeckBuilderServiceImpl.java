package com.breixo.culo.domain.service.dealing;

import com.breixo.culo.domain.constants.DeckConstants;
import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.card.enums.Suit;
import com.breixo.culo.domain.port.input.card.CardFactoryService;
import com.breixo.culo.domain.port.input.dealing.DeckBuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** The Class DeckBuilderServiceImpl. */
@Service
@RequiredArgsConstructor
public class DeckBuilderServiceImpl implements DeckBuilderService {

    /** The card factory service. */
    private final CardFactoryService cardFactoryService;

    /** {@inheritDoc} */
    @Override
    public List<Card> buildShuffledDeck() {

        final var deck = Arrays.stream(Suit.values())
                .flatMap(suit -> DeckConstants.DECK_NUMBERS.stream()
                        .map(number -> this.cardFactoryService.buildCard(suit, number)))
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(deck);
        return deck;
    }
}
