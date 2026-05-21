package com.breixo.culo.domain;

import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.card.enums.Suit;
import com.breixo.culo.domain.service.card.CardFactoryServiceImpl;
import com.breixo.culo.domain.service.quad.QuadDiscardServiceImpl;
import com.breixo.culo.domain.service.room.RoomMembershipServiceImpl;
import com.breixo.culo.testsupport.RoomTestFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** The Class QuadDiscardServiceTest. */
class QuadDiscardServiceTest {

    /** The room membership service. */
    private final RoomMembershipServiceImpl roomMembershipService = new RoomMembershipServiceImpl();

    /** The quad discard service. */
    private final QuadDiscardServiceImpl quadDiscardService = new QuadDiscardServiceImpl();

    /** The card factory service. */
    private final CardFactoryServiceImpl cardFactoryService = new CardFactoryServiceImpl();

    /** Test discard quads when four cards of same number then removes them. */
    @Test
    void testDiscardQuads_whenFourCardsOfSameNumber_thenRemovesThem() {
        final var player = RoomTestFactory.player("p1", "c1", "A");
        var room = this.roomMembershipService.addPlayer(
                RoomTestFactory.emptyRoom("ABCD", "p1"), player);
        final Map<String, List<Card>> hands = new HashMap<>();
        hands.put("p1", new ArrayList<>(List.of(
                this.card(Suit.OROS, 4),
                this.card(Suit.COPAS, 4),
                this.card(Suit.ESPADAS, 4),
                this.card(Suit.BASTOS, 4),
                this.card(Suit.OROS, 7)
        )));
        room = RoomTestFactory.withHands(room, hands);
        room = RoomTestFactory.withPlayerOrder(room, List.of("p1"));

        final var discardQuadsResult = this.quadDiscardService.discardQuads(room, "p1");

        assertThat(discardQuadsResult.events()).hasSize(1);
        assertThat(discardQuadsResult.events().getFirst().value()).isEqualTo(4);
        assertThat(discardQuadsResult.events().getFirst().cards()).hasSize(4);
        assertThat(discardQuadsResult.room().gameSession().hands().get("p1")).hasSize(1);
        assertThat(discardQuadsResult.room().gameSession().hands().get("p1").getFirst().number()).isEqualTo(7);
    }

    /** Test discard quads when three of a kind then does nothing. */
    @Test
    void testDiscardQuads_whenThreeOfAKind_thenDoesNothing() {
        final var player = RoomTestFactory.player("p1", "c1", "A");
        var room = this.roomMembershipService.addPlayer(
                RoomTestFactory.emptyRoom("ABCD", "p1"), player);
        final Map<String, List<Card>> hands = new HashMap<>();
        hands.put("p1", new ArrayList<>(List.of(
                this.card(Suit.OROS, 1),
                this.card(Suit.COPAS, 1),
                this.card(Suit.ESPADAS, 1)
        )));
        room = RoomTestFactory.withHands(room, hands);
        room = RoomTestFactory.withPlayerOrder(room, List.of("p1"));

        final var discardQuadsResult = this.quadDiscardService.discardQuads(room, "p1");

        assertThat(discardQuadsResult.events()).isEmpty();
        assertThat(discardQuadsResult.room().gameSession().hands().get("p1")).hasSize(3);
    }

    /**
     * Card.
     *
     * @param suit   the suit
     * @param number the number
     * @return the card
     */
    private Card card(final Suit suit, final Integer number) {
        return this.cardFactoryService.buildCard(suit, number);
    }
}
