package com.breixo.culo.domain.service.player;

import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.player.PlayerRoleService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** The Class PlayerRoleServiceImpl. */
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
                .map(player -> player.id())
                .findFirst();
    }

    /** {@inheritDoc} */
    @Override
    public Room assignRoles(final Room room) {

        final var finishOrder = room.gameSession().finishOrder();
        final var finishOrderSize = finishOrder.size();
        final var updatedPlayers = room.roomLobby().players().stream()
                .map(player -> {
                    final Integer playerIndex = finishOrder.indexOf(player.id());

                    if (Integer.compare(playerIndex, 0) < 0) {
                        return player;
                    }

                    final PlayerRole playerRole;

                    if (Integer.valueOf(0).equals(playerIndex)) {
                        playerRole = PlayerRole.GANADOR;
                    } else if (Integer.valueOf(finishOrderSize - 1).equals(playerIndex)) {
                        playerRole = PlayerRole.CULO;
                    } else if (Integer.valueOf(finishOrderSize - 2).equals(playerIndex)) {
                        playerRole = PlayerRole.PENULTIMO;
                    } else if (Integer.valueOf(1).equals(playerIndex)) {
                        playerRole = PlayerRole.SUBCAMPEON;
                    } else {
                        playerRole = PlayerRole.NONE;
                    }

                    return player.toBuilder().role(playerRole).build();
                })
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
                .map(player -> rolesByPlayer.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(player.id()))
                        .findFirst()
                        .map(entry -> player.toBuilder().role(entry.getKey()).build())
                        .orElse(player))
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
        EXCHANGE_ROLES.forEach(playerRole -> {
            final var playerId = this.getPlayerIdByRole(room, playerRole);
            playerId.ifPresent(id -> roles.put(playerRole, id));
        });
        return roles;
    }

    /** {@inheritDoc} */
    @Override
    public boolean needsPostDealExchange(final Map<PlayerRole, String> rolesBeforeDeal) {

        final var hasGanador = rolesBeforeDeal.containsKey(PlayerRole.GANADOR);
        final var hasCulo = rolesBeforeDeal.containsKey(PlayerRole.CULO);
        return BooleanUtils.isTrue(hasGanador) && BooleanUtils.isTrue(hasCulo);
    }
}
