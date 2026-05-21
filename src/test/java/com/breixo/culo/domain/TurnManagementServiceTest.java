package com.breixo.culo.domain;

import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.card.enums.Suit;
import com.breixo.culo.domain.service.card.CardFactoryServiceImpl;
import com.breixo.culo.domain.service.card.CardRankResolverServiceImpl;
import com.breixo.culo.domain.service.play.PlayBuilderServiceImpl;
import com.breixo.culo.domain.service.round.RoundServiceImpl;
import com.breixo.culo.domain.service.turn.TurnManagementServiceImpl;
import com.breixo.culo.testsupport.RoomTestFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** The Class TurnManagementServiceTest. */
class TurnManagementServiceTest {

    /** The card factory service. */
    private final CardFactoryServiceImpl cardFactoryService = new CardFactoryServiceImpl();

    /** The play builder service. */
    private final PlayBuilderServiceImpl playBuilderService = new PlayBuilderServiceImpl(this.cardFactoryService);

    /** The round service. */
    private final RoundServiceImpl roundService = new RoundServiceImpl(new CardRankResolverServiceImpl());

    /** The turn management service. */
    private final TurnManagementServiceImpl turnManagementService =
            new TurnManagementServiceImpl(this.roundService);

    /** Test finish round and set opener when last player is out then opens with next active player. */
    @Test
    void testFinishRoundAndSetOpener_whenLastPlayerIsOut_thenOpensWithNextActivePlayer() {
        final var player1 = RoomTestFactory.player("p1", "c-p1", "A");
        final var player2 = RoomTestFactory.player("p2", "c-p2", "B");
        final var player3 = RoomTestFactory.player("p3", "c-p3", "C");
        final var player4 = RoomTestFactory.player("p4", "c-p4", "D");
        var room = RoomTestFactory.roomWithPlayers("ABCD", "p1", List.of(player1, player2, player3, player4));
        room = RoomTestFactory.withPlayerOrder(room, List.of("p1", "p2", "p3", "p4"));
        room = RoomTestFactory.withCurrentPlayerIndex(room, 1);

        final Map<String, List<Card>> hands = new HashMap<>();
        hands.put("p1", new ArrayList<>(List.of(this.card(4))));
        hands.put("p2", new ArrayList<>());
        hands.put("p3", new ArrayList<>(List.of(this.card(5), this.card(6))));
        hands.put("p4", new ArrayList<>(List.of(this.card(7))));
        room = RoomTestFactory.withHands(room, hands);

        final var play = this.playBuilderService.buildPlay(List.of(this.card(4)));
        final var updatedRound = this.roundService.registerPlay(
                room.gameSession().currentRound(), play, "p2");
        room = RoomTestFactory.withCurrentRound(room, updatedRound);

        room = this.turnManagementService.finishRoundAndSetOpener(room);

        final var currentPlayerId = room.gameSession().playerOrder().get(room.gameSession().currentPlayerIndex());
        assertThat(currentPlayerId).isEqualTo("p3");
        assertThat(room.gameSession().currentRound().lastRank()).isNull();
    }

    /**
     * Card.
     *
     * @param number the number
     * @return the card
     */
    private Card card(final Integer number) {
        return this.cardFactoryService.buildCard(Suit.OROS, number);
    }
}
