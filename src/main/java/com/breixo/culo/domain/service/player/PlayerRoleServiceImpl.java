package com.breixo.culo.domain.service.player;

import com.breixo.culo.domain.constants.FinishOrderConstants;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.player.PlayerRoleService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The Class PlayerRoleServiceImpl.
 */
@Service
public class PlayerRoleServiceImpl implements PlayerRoleService {

    /** The Constant EXCHANGE_ROLES. */
    private static final List<PlayerRole> EXCHANGE_ROLES = List.of(
            PlayerRole.GANADOR,
            PlayerRole.CULO,
            PlayerRole.SUBCAMPEON,
            PlayerRole.PENULTIMO);

    /** {@inheritDoc} */
    @Override
    public Optional<String> getPlayerIdByRole(final Room room, final PlayerRole role) {

        return room.roomLobby().players().stream()
                .filter(player -> role.getId().equals(player.role().getId()))
                .map(Player::id)
                .findFirst();
    }

    /** {@inheritDoc} */
    @Override
    public Room assignRoles(final Room room) {

        final var finishOrder = room.gameSession().finishOrder();
        final var finishOrderSize = finishOrder.size();
        final var updatedPlayers = room.roomLobby().players().stream()
                .map(player -> this.assignRoleFromFinishOrder(player, finishOrder, finishOrderSize))
                .toList();

        return room.toBuilder()
                .roomLobby(room.roomLobby().toBuilder()
                        .players(updatedPlayers)
                        .build())
                .gameSession(room.gameSession().toBuilder()
                        .lastCuloId(finishOrder.getLast())
                        .build())
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public Room updatePlayerRoles(final Room room, final Map<PlayerRole, String> rolesByPlayer) {

        final var updatedPlayers = room.roomLobby().players().stream()
                .map(player -> this.applyRoleFromMap(player, rolesByPlayer))
                .toList();

        return room.toBuilder()
                .roomLobby(room.roomLobby().toBuilder()
                        .players(updatedPlayers)
                        .build())
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public Room resetPlayerRoles(final Room room) {

        final var updatedPlayers = room.roomLobby().players().stream()
                .map(player -> player.toBuilder().role(PlayerRole.NONE).build())
                .toList();

        return room.toBuilder()
                .roomLobby(room.roomLobby().toBuilder()
                        .players(updatedPlayers)
                        .build())
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public Map<PlayerRole, String> captureExchangeRoles(final Room room) {

        final var roles = new EnumMap<PlayerRole, String>(PlayerRole.class);

        for (final var playerRole : EXCHANGE_ROLES) {
            this.captureRoleIfPresent(room, playerRole, roles);
        }

        return roles;
    }

    /** {@inheritDoc} */
    @Override
    public boolean needsPostDealExchange(final Map<PlayerRole, String> rolesBeforeDeal) {

        final var hasGanador = rolesBeforeDeal.containsKey(PlayerRole.GANADOR);
        final var hasCulo = rolesBeforeDeal.containsKey(PlayerRole.CULO);
        return BooleanUtils.isTrue(hasGanador) && BooleanUtils.isTrue(hasCulo);
    }

    /**
	 * Apply role from map.
	 *
	 * @param player        the player
	 * @param rolesByPlayer the roles by player
	 * @return the player
	 */
    private Player applyRoleFromMap(final Player player, final Map<PlayerRole, String> rolesByPlayer) {

        for (final var entry : rolesByPlayer.entrySet()) {
            if (entry.getValue().equals(player.id())) {
                return player.toBuilder().role(entry.getKey()).build();
            }
        }

        return player;
    }

    /**
	 * Capture role if present.
	 *
	 * @param room       the room
	 * @param playerRole the player role
	 * @param roles      the roles
	 */
    private void captureRoleIfPresent(
            final Room room,
            final PlayerRole playerRole,
            final Map<PlayerRole, String> roles) {

        final var playerId = this.getPlayerIdByRole(room, playerRole);
        playerId.ifPresent(id -> roles.put(playerRole, id));
    }

    /**
	 * Assign role from finish order.
	 *
	 * @param player          the player
	 * @param finishOrder     the finish order
	 * @param finishOrderSize the finish order size
	 * @return the player
	 */
    private Player assignRoleFromFinishOrder(
            final Player player,
            final List<String> finishOrder,
            final int finishOrderSize) {

        final var finishIndex = finishOrder.indexOf(player.id());

        if (finishIndex == FinishOrderConstants.NOT_IN_FINISH_ORDER) {
            return player;
        }

        final var playerRole = this.resolveRoleForFinishIndex(finishIndex, finishOrderSize);
        return player.toBuilder().role(playerRole).build();
    }

    /**
	 * Resolve role for finish index.
	 *
	 * @param finishIndex     the finish index
	 * @param finishOrderSize the finish order size
	 * @return the player role
	 */
    private PlayerRole resolveRoleForFinishIndex(final int finishIndex, final int finishOrderSize) {

        if (finishIndex == FinishOrderConstants.GANADOR_FINISH_INDEX) {
            return PlayerRole.GANADOR;
        }

        if (finishIndex == finishOrderSize - 1) {
            return PlayerRole.CULO;
        }

        if (finishIndex == finishOrderSize - 2) {
            return PlayerRole.PENULTIMO;
        }

        if (finishIndex == FinishOrderConstants.SUBCAMPEON_FINISH_INDEX) {
            return PlayerRole.SUBCAMPEON;
        }

        return PlayerRole.NONE;
    }
}
