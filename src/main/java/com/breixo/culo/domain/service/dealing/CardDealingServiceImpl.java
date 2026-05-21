package com.breixo.culo.domain.service.dealing;

import com.breixo.culo.domain.constants.CardSortValueConstants;
import com.breixo.culo.domain.constants.GameConstants;
import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.card.enums.Suit;
import com.breixo.culo.domain.model.play.Round;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.card.CardFactoryService;
import com.breixo.culo.domain.port.input.dealing.CardDealingService;
import com.breixo.culo.domain.port.input.dealing.DeckBuilderService;
import com.breixo.culo.domain.port.input.player.HandManagementService;
import com.breixo.culo.domain.port.input.player.PlayerRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** The Class CardDealingServiceImpl. */
@Service
@RequiredArgsConstructor
public class CardDealingServiceImpl implements CardDealingService {

    /** The deck builder service. */
    private final DeckBuilderService deckBuilderService;

    /** The card factory service. */
    private final CardFactoryService cardFactoryService;

    /** The hand management service. */
    private final HandManagementService handManagementService;

    /** The player role service. */
    private final PlayerRoleService playerRoleService;

    /** {@inheritDoc} */
    @Override
    public Room dealCards(final Room room) {

        final var deck = this.deckBuilderService.buildShuffledDeck();
        final var playerOrder = room.roomLobby().players().stream()
                .map(player -> player.id())
                .collect(Collectors.toCollection(ArrayList::new));

        final var dealerId = Optional.ofNullable(room.gameSession().lastCuloId())
                .orElse(room.roomLobby().hostPlayerId());
        final var dealerIdx = playerOrder.indexOf(dealerId);
        if (dealerIdx > 0) {
            Collections.rotate(playerOrder, -dealerIdx);
        }

        final var hands = new HashMap<String, List<Card>>();
        playerOrder.forEach(playerId -> hands.put(playerId, new ArrayList<>()));

        final int playerCount = playerOrder.size();
        for (int cardIndex = 0; cardIndex < deck.size(); cardIndex++) {
            hands.get(playerOrder.get(cardIndex % playerCount)).add(deck.get(cardIndex));
        }

        final var immutableHands = hands.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> List.copyOf(entry.getValue())));

        final var resetPlayers = room.roomLobby().players().stream()
                .map(player -> player.toBuilder().role(PlayerRole.NONE).build())
                .toList();

        final var currentPlayerIndex = Objects.isNull(room.gameSession().lastCuloId())
                ? this.findTwoOfOrosPlayerIndex(room.toBuilder()
                        .gameSession(room.gameSession().toBuilder()
                                .hands(immutableHands)
                                .playerOrder(playerOrder)
                                .build())
                        .build())
                : 0;

        return room.toBuilder()
                .roomLobby(room.roomLobby().toBuilder()
                        .players(resetPlayers)
                        .build())
                .gameSession(room.gameSession().toBuilder()
                        .hands(immutableHands)
                        .playerOrder(playerOrder)
                        .currentPlayerIndex(currentPlayerIndex)
                        .currentRound(Round.builder()
                                .requirement(0)
                                .lastCardNumber(0)
                                .playersPassedSinceLastPlay(new HashSet<>())
                                .lastPlayedCards(List.of())
                                .build())
                        .playEpoch(room.gameSession().playEpoch() + 1)
                        .finishOrder(List.of())
                        .pendingQuadDiscards(List.of())
                        .build())
                .exchangeState(room.exchangeState().toBuilder()
                        .pendingGanadorToCulo(List.of())
                        .pendingSubcampeonToPenultimo(List.of())
                        .exchangeDone(Set.of())
                        .build())
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public Integer findTwoOfOrosPlayerIndex(final Room room) {

        final var twoOfOros = this.cardFactoryService.buildCard(Suit.OROS, 2);
        for (Integer playerIndex = 0; Integer.compare(playerIndex, room.gameSession().playerOrder().size()) < 0; playerIndex++) {
            if (room.gameSession().hands()
                    .get(room.gameSession().playerOrder().get(playerIndex))
                    .contains(twoOfOros)) {
                return playerIndex;
            }
        }
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public Room transferHighestCards(
            final Room room,
            final String giverId,
            final String receiverId,
            final Integer count) {

        final var giverHand = room.gameSession().hands().getOrDefault(giverId, List.of());
        final var bestCards = giverHand.stream()
                .sorted((cardA, cardB) -> Integer.compare(this.cardSortValue(cardB), this.cardSortValue(cardA)))
                .limit(count)
                .toList();

        final var roomWithoutCards = this.handManagementService.removeCardsFromHand(room, giverId, bestCards);
        return this.handManagementService.addCardsToHand(roomWithoutCards, receiverId, bestCards);
    }

    /**
     * Card sort value.
     *
     * @param card the card
     * @return the integer
     */
    private Integer cardSortValue(final Card card) {

        if (Integer.valueOf(1).equals(card.number())) {
            return CardSortValueConstants.AS_SORT_VALUE;
        }
        return card.number();
    }
}
