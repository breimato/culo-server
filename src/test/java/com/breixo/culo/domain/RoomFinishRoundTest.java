package com.breixo.culo.domain;

import com.breixo.culo.domain.model.Card;
import com.breixo.culo.domain.model.Play;
import com.breixo.culo.domain.model.Player;
import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.model.Suit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The Class RoomFinishRoundTest.
 */
class RoomFinishRoundTest {

  /**
	 * Finish round and set opener when last player is out opens with next active
	 * player.
	 */
  @Test
  void finishRoundAndSetOpener_whenLastPlayerIsOut_opensWithNextActivePlayer() {
 
    final var room = new Room("ABCD", "p1");
    addPlayer(room, "p1", "A");
    addPlayer(room, "p2", "B");
    addPlayer(room, "p3", "C");
    addPlayer(room, "p4", "D");
    room.getPlayerOrder().addAll(List.of("p1", "p2", "p3", "p4"));
    room.setCurrentPlayerIndex(1);

    room.getHands().put("p1", new java.util.ArrayList<>(List.of(card(4))));
    room.getHands().put("p2", new java.util.ArrayList<>());
    room.getHands().put("p3", new java.util.ArrayList<>(List.of(card(5), card(6))));
    room.getHands().put("p4", new java.util.ArrayList<>(List.of(card(7))));

    final var play = Play.builder()
        .cards(List.of(card(4)))
        .build();
    room.getCurrentRound().registerPlay(play, "p2");

    room.finishRoundAndSetOpener();

    assertThat(room.getCurrentPlayerId()).isEqualTo("p3");
    assertThat(room.getCurrentRound().isOpen()).isTrue();
  }

  /**
	 * Adds the player.
	 *
	 * @param room the room
	 * @param id   the id
	 * @param nick the nick
	 */
  private static void addPlayer(final Room room, final String id, final String nick) {
    room.addPlayer(Player.builder().id(id).clientId("c-" + id).nick(nick).build());
    room.getHands().put(id, new java.util.ArrayList<>());
  }

  /**
	 * Card.
	 *
	 * @param number the number
	 * @return the card
	 */
  private static Card card(final int number) {
    return Card.builder().suit(Suit.OROS).number(number).build();
  }
}
