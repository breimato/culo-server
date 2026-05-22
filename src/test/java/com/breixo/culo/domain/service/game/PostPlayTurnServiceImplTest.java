package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.model.game.PlayTraits;
import com.breixo.culo.domain.model.game.PlayerElimination;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.game.RoundClosingService;
import com.breixo.culo.domain.port.input.game.TurnManagementService;
import com.breixo.culo.domain.port.input.player.PlayerEliminationService;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
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
 * The Class PostPlayTurnServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class PostPlayTurnServiceImplTest {

    /** The post play turn service. */
    @InjectMocks
    PostPlayTurnServiceImpl postPlayTurnService;

    /** The game context service. */
    @Mock
    GameContextService gameContextService;

    /** The player elimination service. */
    @Mock
    PlayerEliminationService playerEliminationService;

    /** The room phase service. */
    @Mock
    RoomPhaseService roomPhaseService;

    /** The turn management service. */
    @Mock
    TurnManagementService turnManagementService;

    /** The round closing service. */
    @Mock
    RoundClosingService roundClosingService;

    /**
	 * Test apply when game finished then phase is dealing.
	 */
    @Test
    void testApply_whenGameFinished_thenPhaseIsDealing() {
        
        // Given
        final var player = Instancio.create(Player.class);
        final var room = Instancio.create(Room.class);
        final var roomAfterOut = Instancio.create(Room.class);
        final var roomWithPhase = Instancio.create(Room.class);
        final var playerElimination = PlayerElimination.builder()
                .room(roomAfterOut)
                .gameFinished(true)
                .build();
        final var playTraits = PlayTraits.builder().plin(false).isAsOros(false).build();

        // When
        when(this.gameContextService.isPlayerOut(room, player.id())).thenReturn(true);
        when(this.playerEliminationService.registerPlayerOut(room, player.id())).thenReturn(playerElimination);
        when(this.roomPhaseService.withPhase(roomAfterOut, GamePhase.DEALING)).thenReturn(roomWithPhase);
        final var turnAfterPlay = this.postPlayTurnService.apply(room, player, playTraits);

        // Then
        verify(this.gameContextService, times(1)).isPlayerOut(room, player.id());
        verify(this.playerEliminationService, times(1)).registerPlayerOut(room, player.id());
        verify(this.roomPhaseService, times(1)).withPhase(roomAfterOut, GamePhase.DEALING);
        assertTrue(turnAfterPlay.gameFinished());
        assertFalse(turnAfterPlay.roundClosedByPlin());
        assertEquals(roomWithPhase, turnAfterPlay.room());
    }

    /**
	 * Test apply when as oros and player was out then advances turn.
	 */
    @Test
    void testApply_whenAsOrosAndPlayerWasOut_thenAdvancesTurn() {
        
        // Given
        final var player = Instancio.create(Player.class);
        final var room = Instancio.create(Room.class);
        final var roomAfterOut = Instancio.create(Room.class);
        final var roomAfterTurn = Instancio.create(Room.class);
        final var playerElimination = PlayerElimination.builder()
                .room(roomAfterOut)
                .gameFinished(false)
                .build();
        final var playTraits = PlayTraits.builder().plin(false).isAsOros(true).build();

        // When
        when(this.gameContextService.isPlayerOut(room, player.id())).thenReturn(true);
        when(this.playerEliminationService.registerPlayerOut(room, player.id())).thenReturn(playerElimination);
        when(this.turnManagementService.advanceTurn(roomAfterOut, false)).thenReturn(roomAfterTurn);
        final var turnAfterPlay = this.postPlayTurnService.apply(room, player, playTraits);

        // Then
        verify(this.gameContextService, times(1)).isPlayerOut(room, player.id());
        verify(this.playerEliminationService, times(1)).registerPlayerOut(room, player.id());
        verify(this.turnManagementService, times(1)).advanceTurn(roomAfterOut, false);
        assertFalse(turnAfterPlay.gameFinished());
        assertEquals(roomAfterTurn, turnAfterPlay.room());
    }
}
