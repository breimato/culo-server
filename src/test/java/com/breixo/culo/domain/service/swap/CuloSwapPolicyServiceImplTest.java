package com.breixo.culo.domain.service.swap;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.CuloSwapState;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class CuloSwapPolicyServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class CuloSwapPolicyServiceImplTest {

    /** The culo swap policy service. */
    @InjectMocks
    CuloSwapPolicyServiceImpl culoSwapPolicyService;

    /** The player lookup service. */
    @Mock
    PlayerLookupService playerLookupService;

    /**
	 * Test validate no active swap when initiator id is set then throw game
	 * exception.
	 */
    @Test
    void testValidateNoActiveSwap_whenInitiatorIdIsSet_thenThrowGameException() {
        
        // Given
        final var culoSwapState = Instancio.of(CuloSwapState.class)
                .set(field(CuloSwapState::initiatorId), Instancio.create(String.class))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::culoSwapState), culoSwapState)
                .create();

        // When
        final var gameException = assertThrows(
                GameException.class,
                () -> this.culoSwapPolicyService.validateNoActiveSwap(room));

        // Then
        assertEquals(GameExceptionConstants.SWAP_ALREADY_ACTIVE, gameException.getMessage());
    }

    /**
	 * Test validate target exists when player not found then throw room exception.
	 */
    @Test
    void testValidateTargetExists_whenPlayerNotFound_thenThrowRoomException() {
        
        // Given
        final var room = Instancio.create(Room.class);
        final var targetPlayerId = Instancio.create(String.class);

        // When
        when(this.playerLookupService.findPlayerById(room, targetPlayerId)).thenReturn(Optional.empty());
        final var roomException = assertThrows(
                RoomException.class,
                () -> this.culoSwapPolicyService.validateTargetExists(room, targetPlayerId));

        // Then
        verify(this.playerLookupService, times(1)).findPlayerById(room, targetPlayerId);
        assertEquals(RoomExceptionConstants.PLAYER_NOT_IN_ROOM, roomException.getMessage());
    }

    /**
	 * Test validate not already voted when vote exists then throw game exception.
	 */
    @Test
    void testValidateNotAlreadyVoted_whenVoteExists_thenThrowGameException() {
        
        // Given
        final var player = Instancio.create(Player.class);
        final Map<String, Boolean> votes = new HashMap<>();
        votes.put(player.id(), true);
        final var culoSwapState = Instancio.of(CuloSwapState.class)
                .set(field(CuloSwapState::votes), votes)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::culoSwapState), culoSwapState)
                .create();

        // When
        final var gameException = assertThrows(
                GameException.class,
                () -> this.culoSwapPolicyService.validateNotAlreadyVoted(room, player));

        // Then
        assertEquals(GameExceptionConstants.SWAP_ALREADY_VOTED, gameException.getMessage());
    }
}
