package com.breixo.culo.domain.service.player;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.room.GameContextService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class PlayerEliminationServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
class PlayerEliminationServiceImplTest {

    /** The game context service. */
    @Mock
    GameContextService gameContextService;

    /** The player role service. */
    final PlayerRoleServiceImpl playerRoleService = new PlayerRoleServiceImpl();

    /** The player elimination service. */
    PlayerEliminationServiceImpl playerEliminationService;

    /**
	 * Sets the up.
	 */
    @BeforeEach
    void setUp() {
        this.playerEliminationService = new PlayerEliminationServiceImpl(
                this.gameContextService,
                this.playerRoleService);
    }

    /**
	 * Test register player out when multiple active players then game not finished.
	 */
    @Test
    void testRegisterPlayerOut_whenMultipleActivePlayers_thenGameNotFinished() {
        
        // Given
        final var playerId = Instancio.create(String.class);
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::finishOrder), List.of())
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::gameSession), gameSession)
                .create();
        final ArgumentCaptor<Room> roomArgumentCaptor = ArgumentCaptor.forClass(Room.class);

        // When
        when(this.gameContextService.activePlayerIds(roomArgumentCaptor.capture()))
                .thenReturn(List.of(Instancio.create(String.class), Instancio.create(String.class)));
        final var playerElimination = this.playerEliminationService.registerPlayerOut(room, playerId);

        // Then
        verify(this.gameContextService, times(1)).activePlayerIds(roomArgumentCaptor.getValue());
        assertFalse(playerElimination.gameFinished());
        assertEquals(List.of(playerId), playerElimination.room().gameSession().finishOrder());
    }

    /**
	 * Test register player out when last active player then finish game with roles.
	 */
    @Test
    void testRegisterPlayerOut_whenLastActivePlayer_thenFinishGameWithRoles() {
        
        // Given
        final var eliminatedPlayerId = "player-eliminated";
        final var secondPlayerId = "player-second";
        final var thirdPlayerId = "player-third";
        final var remainingPlayerId = "player-remaining";
        final var card = Instancio.create(Card.class);
        final var eliminatedPlayer = Instancio.of(Player.class)
                .set(field(Player::id), eliminatedPlayerId)
                .set(field(Player::role), PlayerRole.NONE)
                .create();
        final var secondPlayer = Instancio.of(Player.class)
                .set(field(Player::id), secondPlayerId)
                .set(field(Player::role), PlayerRole.NONE)
                .create();
        final var thirdPlayer = Instancio.of(Player.class)
                .set(field(Player::id), thirdPlayerId)
                .set(field(Player::role), PlayerRole.NONE)
                .create();
        final var remainingPlayer = Instancio.of(Player.class)
                .set(field(Player::id), remainingPlayerId)
                .set(field(Player::role), PlayerRole.NONE)
                .create();
        final var hands = Map.of(
                eliminatedPlayerId, List.<Card>of(),
                secondPlayerId, List.<Card>of(),
                thirdPlayerId, List.<Card>of(),
                remainingPlayerId, List.of(card));
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::hands), hands)
                .set(field(GameSession::playerOrder), List.of(
                        eliminatedPlayerId,
                        secondPlayerId,
                        thirdPlayerId,
                        remainingPlayerId))
                .set(field(GameSession::finishOrder), List.of(eliminatedPlayerId, secondPlayerId, thirdPlayerId))
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(eliminatedPlayer, secondPlayer, thirdPlayer, remainingPlayer))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .set(field(Room::gameSession), gameSession)
                .create();
        final ArgumentCaptor<Room> roomArgumentCaptor = ArgumentCaptor.forClass(Room.class);

        // When
        when(this.gameContextService.activePlayerIds(roomArgumentCaptor.capture()))
                .thenAnswer(invocation -> {
                    final Room capturedRoom = invocation.getArgument(0);
                    return capturedRoom.gameSession().playerOrder().stream()
                            .filter(activePlayerId -> Boolean.FALSE.equals(
                                    capturedRoom.gameSession().hands().get(activePlayerId).isEmpty()))
                            .toList();
                });
        final var playerElimination = this.playerEliminationService.registerPlayerOut(room, remainingPlayerId);

        // Then
        verify(this.gameContextService, times(2)).activePlayerIds(roomArgumentCaptor.getValue());
        assertTrue(playerElimination.gameFinished());
        assertEquals(
                List.of(eliminatedPlayerId, secondPlayerId, thirdPlayerId, remainingPlayerId, remainingPlayerId),
                playerElimination.room().gameSession().finishOrder());
        assertEquals(remainingPlayerId, playerElimination.room().gameSession().lastCuloId());
        assertEquals(PlayerRole.GANADOR, playerElimination.room().roomLobby().players().stream()
                .filter(player -> player.id().equals(eliminatedPlayerId))
                .findFirst()
                .orElseThrow()
                .role());
    }
}
