package com.breixo.culo.domain.service.cards;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.Suit;
import com.breixo.culo.domain.model.game.Round;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.cards.CardFactoryService;
import com.breixo.culo.domain.port.input.cards.CardDealingService;
import com.breixo.culo.domain.port.input.cards.DeckBuilderService;
import com.breixo.culo.domain.port.input.player.HandManagementService;
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

/**
 * The Class CardDealingServiceImpl.
 */
@Service
@RequiredArgsConstructor
public class CardDealingServiceImpl implements CardDealingService {

    /** The Constant AS_SORT_VALUE. */
    public static final Integer AS_SORT_VALUE = 999;

    /** The Constant AS_CARD_NUMBER. */
    private static final Integer AS_CARD_NUMBER = 1;

    /** The Constant TWO_OF_OROS_NUMBER. */
    private static final Integer TWO_OF_OROS_NUMBER = 2;

    /** The Constant FIRST_PLAYER_INDEX. */
    private static final int FIRST_PLAYER_INDEX = 0;

    /** The deck builder service. */
    private final DeckBuilderService deckBuilderService;

    /** The card factory service. */
    private final CardFactoryService cardFactoryService;

    /** The hand management service. */
    private final HandManagementService handManagementService;

    /** {@inheritDoc} */
    @Override
    public Room dealCards(final Room room) {

        final var deck = this.deckBuilderService.buildShuffledDeck();
        final var playerOrder = this.buildDealerRotatedPlayerOrder(room);
        final var hands = this.distributeHands(deck, playerOrder);
        final var playersWithoutRoles = this.resetPlayersToNone(room);
        final var currentPlayerIndex = this.resolveOpeningPlayerIndex(room, playerOrder, hands);

        return this.assembleRoomAfterDeal(room, playerOrder, hands, playersWithoutRoles, currentPlayerIndex);
    }

    /** {@inheritDoc} */
    @Override
    public Integer findTwoOfOrosPlayerIndex(final Room room) {

        final var twoOfOros = this.cardFactoryService.buildCard(Suit.OROS, TWO_OF_OROS_NUMBER);
        final var playerOrder = room.gameSession().playerOrder();

        for (var playerIndex = FIRST_PLAYER_INDEX; playerIndex < playerOrder.size(); playerIndex++) {

            final var hand = room.gameSession().hands().get(playerOrder.get(playerIndex));

            if (hand.contains(twoOfOros)) {
                return playerIndex;
            }
        }

        return FIRST_PLAYER_INDEX;
    }

    /** {@inheritDoc} */
    @Override
    public Room transferHighestCards(
            final Room room, final String giverId, final String receiverId, final Integer count) {

        final var giverHand = room.gameSession().hands().getOrDefault(giverId, List.of());

        final var bestCards = giverHand.stream()
                .sorted((cardA, cardB) -> Integer.compare(this.cardSortValue(cardB), this.cardSortValue(cardA)))
                .limit(count)
                .toList();

        final var roomWithoutCards = this.handManagementService.removeCardsFromHand(room, giverId, bestCards);
        return this.handManagementService.addCardsToHand(roomWithoutCards, receiverId, bestCards);
    }

    /**
	 * Builds the dealer rotated player order.
	 *
	 * @param room the room
	 * @return the list
	 */
    private List<String> buildDealerRotatedPlayerOrder(final Room room) {

        final var playerOrder = room.roomLobby().players().stream()
                .map(Player::id)
                .collect(Collectors.toCollection(ArrayList::new));

        final var dealerId =
                Optional.ofNullable(room.gameSession().lastCuloId()).orElse(room.roomLobby().hostPlayerId());

        if (playerOrder.indexOf(dealerId) > FIRST_PLAYER_INDEX) {
            Collections.rotate(playerOrder, -playerOrder.indexOf(dealerId));
        }

        return playerOrder;
    }

    /**
	 * Distribute hands.
	 *
	 * @param deck        the deck
	 * @param playerOrder the player order
	 * @return the map
	 */
    private Map<String, List<Card>> distributeHands(final List<Card> deck, final List<String> playerOrder) {

        final var hands = new HashMap<String, List<Card>>();
        final var playerCount = playerOrder.size();

        for (final var playerId : playerOrder) {
            hands.put(playerId, new ArrayList<>());
        }

        for (var cardIndex = FIRST_PLAYER_INDEX; cardIndex < deck.size(); cardIndex++) {

            final var playerId = playerOrder.get(cardIndex % playerCount);
            hands.get(playerId).add(deck.get(cardIndex));
        }

        return hands.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> List.copyOf(entry.getValue())));
    }

    /**
	 * Reset players to none.
	 *
	 * @param room the room
	 * @return the list
	 */
    private List<Player> resetPlayersToNone(final Room room) {

        return room.roomLobby().players().stream()
                .map(player -> player.toBuilder().role(PlayerRole.NONE).build())
                .toList();
    }

    /**
	 * Resolve opening player index.
	 *
	 * @param room        the room
	 * @param playerOrder the player order
	 * @param hands       the hands
	 * @return the integer
	 */
    private Integer resolveOpeningPlayerIndex(
            final Room room, final List<String> playerOrder, final Map<String, List<Card>> hands) {

        if (Objects.nonNull(room.gameSession().lastCuloId())) {
            return FIRST_PLAYER_INDEX;
        }

        final var roomWithHands = room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .hands(hands)
                        .playerOrder(playerOrder)
                        .build())
                .build();

        return this.findTwoOfOrosPlayerIndex(roomWithHands);
    }

    /**
	 * Assemble room after deal.
	 *
	 * @param room                the room
	 * @param playerOrder         the player order
	 * @param hands               the hands
	 * @param playersWithoutRoles the players without roles
	 * @param currentPlayerIndex  the current player index
	 * @return the room
	 */
    private Room assembleRoomAfterDeal(
            final Room room, final List<String> playerOrder, final Map<String, List<Card>> hands,
            final List<Player> playersWithoutRoles, final Integer currentPlayerIndex) {

        return room.toBuilder()
                .roomLobby(room.roomLobby().toBuilder()
                        .players(playersWithoutRoles)
                        .build())
                .gameSession(room.gameSession().toBuilder()
                        .hands(hands)
                        .playerOrder(playerOrder)
                        .currentPlayerIndex(currentPlayerIndex)
                        .currentRound(this.buildFreshRound())
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

    /**
	 * Builds the fresh round.
	 *
	 * @return the round
	 */
    private Round buildFreshRound() {

        return Round.builder()
                .requirement(0)
                .lastCardNumber(0)
                .playersPassedSinceLastPlay(new HashSet<>())
                .lastPlayedCards(List.of())
                .build();
    }

    /**
	 * Card sort value.
	 *
	 * @param card the card
	 * @return the integer
	 */
    private Integer cardSortValue(final Card card) {

        if (AS_CARD_NUMBER.equals(card.number())) {
            return AS_SORT_VALUE;
        }

        return card.number();
    }
}
