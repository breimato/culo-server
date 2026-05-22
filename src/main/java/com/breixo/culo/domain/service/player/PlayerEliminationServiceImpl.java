package com.breixo.culo.domain.service.player;

import com.breixo.culo.domain.constants.GameConstants;
import com.breixo.culo.domain.model.game.PlayerElimination;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.player.PlayerEliminationService;
import com.breixo.culo.domain.port.input.player.PlayerRoleService;
import com.breixo.culo.domain.port.input.room.GameContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

/** The Class PlayerEliminationServiceImpl. */
@Service
@RequiredArgsConstructor
public class PlayerEliminationServiceImpl implements PlayerEliminationService {

    /** The game session context service. */
    private final GameContextService gameSessionContextService;

    /** The player role service. */
    private final PlayerRoleService playerRoleService;

    /** {@inheritDoc} */
    @Override
    public PlayerElimination registerPlayerOut(final Room room, final String playerId) {

        final var finishOrder = new ArrayList<>(room.gameSession().finishOrder());
        finishOrder.add(playerId);

        var roomWithFinishOrder = room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .finishOrder(finishOrder)
                        .build())
                .build();

        final var activePlayerCount = this.gameSessionContextService.activePlayerIds(roomWithFinishOrder).size();
        final var lastPlayerRemaining = Objects.equals(GameConstants.LAST_ACTIVE_PLAYER_COUNT, activePlayerCount);

        if (lastPlayerRemaining) {
            return this.finishGameWithLastActivePlayer(roomWithFinishOrder);
        }

        return PlayerElimination.builder()
                .room(roomWithFinishOrder)
                .gameFinished(false)
                .build();
    }

    private PlayerElimination finishGameWithLastActivePlayer(final Room roomWithFinishOrder) {

        final var culoId = this.gameSessionContextService.activePlayerIds(roomWithFinishOrder).getFirst();

        final var finishOrderWithCulo = new ArrayList<>(roomWithFinishOrder.gameSession().finishOrder());
        finishOrderWithCulo.add(culoId);

        final var roomBeforeRoles = roomWithFinishOrder.toBuilder()
                .gameSession(roomWithFinishOrder.gameSession().toBuilder()
                        .finishOrder(finishOrderWithCulo)
                        .build())
                .build();

        final var roomWithRoles = this.playerRoleService.assignRoles(roomBeforeRoles);

        return PlayerElimination.builder()
                .room(roomWithRoles)
                .gameFinished(true)
                .build();
    }
}
