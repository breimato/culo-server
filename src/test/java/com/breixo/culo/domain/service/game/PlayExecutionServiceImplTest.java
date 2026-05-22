package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.model.game.HandAfterPlay;
import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.game.PlayTraits;
import com.breixo.culo.domain.model.game.TurnAfterPlay;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.game.HandOnPlayService;
import com.breixo.culo.domain.port.input.game.PostPlayTurnService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class PlayExecutionServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class PlayExecutionServiceImplTest {

    /** The play execution service. */
    @InjectMocks
    PlayExecutionServiceImpl playExecutionService;

    /** The hand on play service. */
    @Mock
    HandOnPlayService handOnPlayService;

    /** The post play turn service. */
    @Mock
    PostPlayTurnService postPlayTurnService;

    /**
	 * Test execute when plin and not as oros then plin is true.
	 */
    @Test
    void testExecute_whenPlinAndNotAsOros_thenPlinIsTrue() {
        
        // Given
        final var room = Instancio.create(Room.class);
        final var player = Instancio.create(Player.class);
        final var play = Instancio.create(Play.class);
        final var roomAfterHand = Instancio.create(Room.class);
        final var roomAfterTurn = Instancio.create(Room.class);
        final var playTraits = PlayTraits.builder().plin(true).isAsOros(false).build();
        final var handAfterPlay = HandAfterPlay.builder().room(roomAfterHand).playFlags(playTraits).build();
        final var turnAfterPlay = TurnAfterPlay.builder()
                .room(roomAfterTurn)
                .gameFinished(false)
                .roundClosedByPlin(false)
                .build();

        // When
        when(this.handOnPlayService.apply(room, player, play)).thenReturn(handAfterPlay);
        when(this.postPlayTurnService.apply(roomAfterHand, player, playTraits)).thenReturn(turnAfterPlay);
        final var playExecutionResult = this.playExecutionService.execute(room, player, play);

        // Then
        verify(this.handOnPlayService, times(1)).apply(room, player, play);
        verify(this.postPlayTurnService, times(1)).apply(roomAfterHand, player, playTraits);
        assertEquals(roomAfterTurn, playExecutionResult.room());
        assertTrue(playExecutionResult.plin());
        assertFalse(playExecutionResult.roundClosed());
        assertFalse(playExecutionResult.gameFinished());
    }

    /**
	 * Test execute when as oros then round is closed.
	 */
    @Test
    void testExecute_whenAsOros_thenRoundIsClosed() {
        
        // Given
        final var room = Instancio.create(Room.class);
        final var player = Instancio.create(Player.class);
        final var play = Instancio.create(Play.class);
        final var roomAfterHand = Instancio.create(Room.class);
        final var roomAfterTurn = Instancio.create(Room.class);
        final var playTraits = PlayTraits.builder().plin(false).isAsOros(true).build();
        final var handAfterPlay = HandAfterPlay.builder().room(roomAfterHand).playFlags(playTraits).build();
        final var turnAfterPlay = TurnAfterPlay.builder()
                .room(roomAfterTurn)
                .gameFinished(false)
                .roundClosedByPlin(false)
                .build();

        // When
        when(this.handOnPlayService.apply(room, player, play)).thenReturn(handAfterPlay);
        when(this.postPlayTurnService.apply(roomAfterHand, player, playTraits)).thenReturn(turnAfterPlay);
        final var playExecutionResult = this.playExecutionService.execute(room, player, play);

        // Then
        verify(this.handOnPlayService, times(1)).apply(room, player, play);
        verify(this.postPlayTurnService, times(1)).apply(roomAfterHand, player, playTraits);
        assertTrue(playExecutionResult.roundClosed());
        assertFalse(playExecutionResult.plin());
    }

    /**
	 * Test execute when round closed by plin then round is closed.
	 */
    @Test
    void testExecute_whenRoundClosedByPlin_thenRoundIsClosed() {
        
        // Given
        final var room = Instancio.create(Room.class);
        final var player = Instancio.create(Player.class);
        final var play = Instancio.create(Play.class);
        final var roomAfterHand = Instancio.create(Room.class);
        final var roomAfterTurn = Instancio.create(Room.class);
        final var playTraits = PlayTraits.builder().plin(false).isAsOros(false).build();
        final var handAfterPlay = HandAfterPlay.builder().room(roomAfterHand).playFlags(playTraits).build();
        final var turnAfterPlay = TurnAfterPlay.builder()
                .room(roomAfterTurn)
                .gameFinished(true)
                .roundClosedByPlin(true)
                .build();

        // When
        when(this.handOnPlayService.apply(room, player, play)).thenReturn(handAfterPlay);
        when(this.postPlayTurnService.apply(roomAfterHand, player, playTraits)).thenReturn(turnAfterPlay);
        final var playExecutionResult = this.playExecutionService.execute(room, player, play);

        // Then
        verify(this.handOnPlayService, times(1)).apply(room, player, play);
        verify(this.postPlayTurnService, times(1)).apply(roomAfterHand, player, playTraits);
        assertTrue(playExecutionResult.roundClosed());
        assertTrue(playExecutionResult.gameFinished());
    }
}
