package com.breixo.culo.domain;

import com.breixo.culo.domain.model.Card;
import com.breixo.culo.domain.model.Player;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.model.Suit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoomDiscardQuadsTest {

  @Test
  void discardQuads_removesFourCardsOfSameNumber() {
    final var room = new Room("ABCD", "p1");
    room.addPlayer(Player.builder().id("p1").clientId("c1").nick("A").build());
    room.getHands().put("p1", new java.util.ArrayList<>(java.util.List.of(
        card(Suit.OROS, 4),
        card(Suit.COPAS, 4),
        card(Suit.ESPADAS, 4),
        card(Suit.BASTOS, 4),
        card(Suit.OROS, 7)
    )));
    room.getPlayerOrder().add("p1");

    final var events = room.discardQuads("p1");

    assertThat(events).hasSize(1);
    assertThat(events.getFirst().value()).isEqualTo(4);
    assertThat(events.getFirst().cards()).hasSize(4);
    assertThat(room.getHand("p1")).hasSize(1);
    assertThat(room.getHand("p1").getFirst().number()).isEqualTo(7);
  }

  @Test
  void discardQuads_doesNothingDuringExchangeWithThreeOfAKind() {
    final var room = new Room("ABCD", "p1");
    room.addPlayer(Player.builder().id("p1").clientId("c1").nick("A").build());
    room.getHands().put("p1", new java.util.ArrayList<>(java.util.List.of(
        card(Suit.OROS, 1),
        card(Suit.COPAS, 1),
        card(Suit.ESPADAS, 1)
    )));
    room.getPlayerOrder().add("p1");

    final var events = room.discardQuads("p1");

    assertThat(events).isEmpty();
    assertThat(room.getHand("p1")).hasSize(3);
  }

  private static Card card(final Suit suit, final int number) {
    return Card.builder().suit(suit).number(number).build();
  }
}
