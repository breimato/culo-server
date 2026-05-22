package com.breixo.culo.domain.service.swap;

import com.breixo.culo.domain.model.room.CuloSwapState;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Class CuloServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class CuloServiceImplTest {

    /** The culo service. */
    @InjectMocks
    CuloServiceImpl culoService;

    /**
	 * Test register vote when not all players voted then all players have voted is
	 * false.
	 */
    @Test
    void testRegisterVote_whenNotAllPlayersVoted_thenAllPlayersHaveVotedIsFalse() {
        
        // Given
        final var voter = Instancio.of(Player.class)
                .set(field(Player::id), "voter-id")
                .create();
        final var otherPlayer = Instancio.of(Player.class)
                .set(field(Player::id), "other-id")
                .create();
        final var votes = new HashMap<String, Boolean>();
        final var culoSwapState = Instancio.of(CuloSwapState.class)
                .set(field(CuloSwapState::votes), votes)
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(voter, otherPlayer))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::culoSwapState), culoSwapState)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When
        final var culoSwapVoteCast = this.culoService.registerVote(room, voter.id(), true);

        // Then
        assertFalse(culoSwapVoteCast.allPlayersHaveVoted());
        assertTrue(culoSwapVoteCast.room().culoSwapState().votes().get(voter.id()));
    }

    /**
	 * Test register vote when all players voted then all players have voted is
	 * true.
	 */
    @Test
    void testRegisterVote_whenAllPlayersVoted_thenAllPlayersHaveVotedIsTrue() {
        
        // Given
        final var voter = Instancio.of(Player.class)
                .set(field(Player::id), "voter-id")
                .create();
        final var otherPlayer = Instancio.of(Player.class)
                .set(field(Player::id), "other-id")
                .create();
        final var votes = new HashMap<String, Boolean>();
        votes.put(otherPlayer.id(), true);
        final var culoSwapState = Instancio.of(CuloSwapState.class)
                .set(field(CuloSwapState::votes), votes)
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(voter, otherPlayer))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::culoSwapState), culoSwapState)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When
        final var culoSwapVoteCast = this.culoService.registerVote(room, voter.id(), false);

        // Then
        assertTrue(culoSwapVoteCast.allPlayersHaveVoted());
        assertEquals(2, culoSwapVoteCast.room().culoSwapState().votes().size());
    }

    /**
	 * Test is swap approved when votes empty then return false.
	 */
    @Test
    void testIsSwapApproved_whenVotesEmpty_thenReturnFalse() {
        
        // Given
        final var culoSwapState = Instancio.of(CuloSwapState.class)
                .set(field(CuloSwapState::votes), Map.of())
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::culoSwapState), culoSwapState)
                .create();

        // When / Then
        assertFalse(this.culoService.isSwapApproved(room));
    }

    /**
	 * Test is swap approved when all votes accept then return true.
	 */
    @Test
    void testIsSwapApproved_whenAllVotesAccept_thenReturnTrue() {
        
        // Given
        final var votes = Map.of("p1", true, "p2", true);
        final var culoSwapState = Instancio.of(CuloSwapState.class)
                .set(field(CuloSwapState::votes), votes)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::culoSwapState), culoSwapState)
                .create();

        // When / Then
        assertTrue(this.culoService.isSwapApproved(room));
    }

    /**
	 * Test is swap approved when one vote rejects then return false.
	 */
    @Test
    void testIsSwapApproved_whenOneVoteRejects_thenReturnFalse() {
        
        // Given
        final var votes = Map.of("p1", true, "p2", false);
        final var culoSwapState = Instancio.of(CuloSwapState.class)
                .set(field(CuloSwapState::votes), votes)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::culoSwapState), culoSwapState)
                .create();

        // When / Then
        assertFalse(this.culoService.isSwapApproved(room));
    }

    /**
	 * Test initiate swap when called then set initiator and target.
	 */
    @Test
    void testInitiateSwap_whenCalled_thenSetInitiatorAndTarget() {
        
        // Given
        final var room = Instancio.create(Room.class);

        // When
        final var roomAfterInitiate = this.culoService.initiateSwap(room, "initiator-id", "target-id");

        // Then
        assertEquals("initiator-id", roomAfterInitiate.culoSwapState().initiatorId());
        assertEquals("target-id", roomAfterInitiate.culoSwapState().targetId());
    }

    /**
	 * Test clear swap when called then reset swap state.
	 */
    @Test
    void testClearSwap_whenCalled_thenResetSwapState() {
        
        // Given
        final var votes = new HashMap<String, Boolean>();
        votes.put("p1", true);
        final var culoSwapState = CuloSwapState.builder()
                .initiatorId("initiator-id")
                .targetId("target-id")
                .votes(votes)
                .build();
        final var room = Instancio.of(Room.class)
                .set(field(Room::culoSwapState), culoSwapState)
                .create();

        // When
        final var roomAfterClear = this.culoService.clearSwap(room);

        // Then
        assertTrue(roomAfterClear.culoSwapState().votes().isEmpty());
    }
}
