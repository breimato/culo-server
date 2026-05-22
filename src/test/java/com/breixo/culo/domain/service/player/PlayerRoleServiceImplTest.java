package com.breixo.culo.domain.service.player;

import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The Class Player Role Service Impl Test. */
@ExtendWith(MockitoExtension.class)
class PlayerRoleServiceImplTest {

    /** The player role service. */
    @InjectMocks
    PlayerRoleServiceImpl playerRoleService;

    /** Test get player id by role when role exists then return player id. */
    @Test
    void testGetPlayerIdByRole_whenRoleExists_thenReturnPlayerId() {
        // Given
        final var player = Instancio.of(Player.class)
                .set(field(Player::id), "player-ganador")
                .set(field(Player::role), PlayerRole.GANADOR)
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(player))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When
        final var playerIdOptional = this.playerRoleService.getPlayerIdByRole(room, PlayerRole.GANADOR);

        // Then
        assertTrue(playerIdOptional.isPresent());
        assertEquals("player-ganador", playerIdOptional.get());
    }

    /** Test get player id by role when role missing then return empty. */
    @Test
    void testGetPlayerIdByRole_whenRoleMissing_thenReturnEmpty() {
        // Given
        final var player = Instancio.of(Player.class)
                .set(field(Player::role), PlayerRole.NONE)
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(player))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When
        final var playerIdOptional = this.playerRoleService.getPlayerIdByRole(room, PlayerRole.CULO);

        // Then
        assertTrue(playerIdOptional.isEmpty());
    }

    /** Test assign roles when finish order complete then assign exchange roles. */
    @Test
    void testAssignRoles_whenFinishOrderComplete_thenAssignExchangeRoles() {
        // Given
        final var ganador = Instancio.of(Player.class)
                .set(field(Player::id), "p1")
                .set(field(Player::role), PlayerRole.NONE)
                .create();
        final var subcampeon = Instancio.of(Player.class)
                .set(field(Player::id), "p2")
                .set(field(Player::role), PlayerRole.NONE)
                .create();
        final var penultimo = Instancio.of(Player.class)
                .set(field(Player::id), "p3")
                .set(field(Player::role), PlayerRole.NONE)
                .create();
        final var culo = Instancio.of(Player.class)
                .set(field(Player::id), "p4")
                .set(field(Player::role), PlayerRole.NONE)
                .create();
        final var finishOrder = List.of("p1", "p2", "p3", "p4");
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::finishOrder), finishOrder)
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(ganador, subcampeon, penultimo, culo))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .set(field(Room::gameSession), gameSession)
                .create();

        // When
        final var roomWithRoles = this.playerRoleService.assignRoles(room);

        // Then
        final var playersById = roomWithRoles.roomLobby().players().stream()
                .collect(Collectors.toMap(Player::id, player -> player));
        assertEquals(PlayerRole.GANADOR, playersById.get("p1").role());
        assertEquals(PlayerRole.SUBCAMPEON, playersById.get("p2").role());
        assertEquals(PlayerRole.PENULTIMO, playersById.get("p3").role());
        assertEquals(PlayerRole.CULO, playersById.get("p4").role());
        assertEquals("p4", roomWithRoles.gameSession().lastCuloId());
    }

    /** Test assign roles when player not in finish order then keep role unchanged. */
    @Test
    void testAssignRoles_whenPlayerNotInFinishOrder_thenKeepRoleUnchanged() {
        // Given
        final var player = Instancio.of(Player.class)
                .set(field(Player::id), "outside")
                .set(field(Player::role), PlayerRole.SUBCAMPEON)
                .create();
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::finishOrder), List.of("p1"))
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(player))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .set(field(Room::gameSession), gameSession)
                .create();

        // When
        final var roomWithRoles = this.playerRoleService.assignRoles(room);

        // Then
        assertEquals(PlayerRole.SUBCAMPEON, roomWithRoles.roomLobby().players().getFirst().role());
    }

    /** Test update player roles when map provided then apply roles. */
    @Test
    void testUpdatePlayerRoles_whenMapProvided_thenApplyRoles() {
        // Given
        final var firstPlayer = Instancio.of(Player.class)
                .set(field(Player::id), "p1")
                .set(field(Player::role), PlayerRole.NONE)
                .create();
        final var secondPlayer = Instancio.of(Player.class)
                .set(field(Player::id), "p2")
                .set(field(Player::role), PlayerRole.NONE)
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(firstPlayer, secondPlayer))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();
        final Map<PlayerRole, String> rolesByPlayer = Map.of(
                PlayerRole.GANADOR, "p1",
                PlayerRole.CULO, "p2");

        // When
        final var roomWithRoles = this.playerRoleService.updatePlayerRoles(room, rolesByPlayer);

        // Then
        final var playersById = roomWithRoles.roomLobby().players().stream()
                .collect(Collectors.toMap(Player::id, player -> player));
        assertEquals(PlayerRole.GANADOR, playersById.get("p1").role());
        assertEquals(PlayerRole.CULO, playersById.get("p2").role());
    }

    /** Test reset player roles when called then set all to none. */
    @Test
    void testResetPlayerRoles_whenCalled_thenSetAllToNone() {
        // Given
        final var player = Instancio.of(Player.class)
                .set(field(Player::role), PlayerRole.GANADOR)
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(player))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When
        final var roomWithResetRoles = this.playerRoleService.resetPlayerRoles(room);

        // Then
        assertEquals(PlayerRole.NONE, roomWithResetRoles.roomLobby().players().getFirst().role());
    }

    /** Test capture exchange roles when roles present then capture all exchange roles. */
    @Test
    void testCaptureExchangeRoles_whenRolesPresent_thenCaptureAllExchangeRoles() {
        // Given
        final var ganador = Instancio.of(Player.class)
                .set(field(Player::id), "ganador-id")
                .set(field(Player::role), PlayerRole.GANADOR)
                .create();
        final var subcampeon = Instancio.of(Player.class)
                .set(field(Player::id), "subcampeon-id")
                .set(field(Player::role), PlayerRole.SUBCAMPEON)
                .create();
        final var penultimo = Instancio.of(Player.class)
                .set(field(Player::id), "penultimo-id")
                .set(field(Player::role), PlayerRole.PENULTIMO)
                .create();
        final var culo = Instancio.of(Player.class)
                .set(field(Player::id), "culo-id")
                .set(field(Player::role), PlayerRole.CULO)
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.of(ganador, subcampeon, penultimo, culo))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .create();

        // When
        final var capturedRoles = this.playerRoleService.captureExchangeRoles(room);

        // Then
        assertEquals("ganador-id", capturedRoles.get(PlayerRole.GANADOR));
        assertEquals("subcampeon-id", capturedRoles.get(PlayerRole.SUBCAMPEON));
        assertEquals("penultimo-id", capturedRoles.get(PlayerRole.PENULTIMO));
        assertEquals("culo-id", capturedRoles.get(PlayerRole.CULO));
    }

    /** Test needs post deal exchange when ganador and culo present then return true. */
    @Test
    void testNeedsPostDealExchange_whenGanadorAndCuloPresent_thenReturnTrue() {
        // Given
        final Map<PlayerRole, String> rolesBeforeDeal = new EnumMap<>(PlayerRole.class);
        rolesBeforeDeal.put(PlayerRole.GANADOR, "ganador-id");
        rolesBeforeDeal.put(PlayerRole.CULO, "culo-id");

        // When
        final var needsExchange = this.playerRoleService.needsPostDealExchange(rolesBeforeDeal);

        // Then
        assertTrue(needsExchange);
    }

    /** Test needs post deal exchange when culo missing then return false. */
    @Test
    void testNeedsPostDealExchange_whenCuloMissing_thenReturnFalse() {
        // Given
        final Map<PlayerRole, String> rolesBeforeDeal = Map.of(PlayerRole.GANADOR, "ganador-id");

        // When
        final var needsExchange = this.playerRoleService.needsPostDealExchange(rolesBeforeDeal);

        // Then
        assertFalse(needsExchange);
    }
}
