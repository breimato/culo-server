package com.breixo.culo.domain.service.player;

import com.breixo.culo.domain.model.outcome.RegisterPlayerOutResult;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.player.PlayerOutcomeService;
import com.breixo.culo.domain.port.input.player.PlayerRoleService;
import com.breixo.culo.domain.port.input.session.GameSessionContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/** The Class PlayerOutcomeServiceImpl. */
@Service
@RequiredArgsConstructor
public class PlayerOutcomeServiceImpl implements PlayerOutcomeService {

    /** The game session context service. */
    private final GameSessionContextService gameSessionContextService;

    /** The player role service. */
    private final PlayerRoleService playerRoleService;

    /** {@inheritDoc} */
    @Override
    public Integer getActivePlayerCount(final Room room) {

        return Math.toIntExact(this.gameSessionContextService.activePlayerIds(room).size());
    }

    /** {@inheritDoc} */
    @Override
    public RegisterPlayerOutResult registerPlayerOut(final Room room, final String playerId) {

        final var finishOrder = new ArrayList<>(room.gameSession().finishOrder());
        finishOrder.add(playerId);
        var roomWithFinishOrder = room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .finishOrder(finishOrder)
                        .build())
                .build();

        final Integer activePlayerCount = this.getActivePlayerCount(roomWithFinishOrder);

        if (Integer.valueOf(1).equals(activePlayerCount)) {
            final var culoId = this.gameSessionContextService.activePlayerIds(roomWithFinishOrder).getFirst();
            final var finishOrderWithCulo = new ArrayList<>(roomWithFinishOrder.gameSession().finishOrder());
            finishOrderWithCulo.add(culoId);
            roomWithFinishOrder = roomWithFinishOrder.toBuilder()
                    .gameSession(roomWithFinishOrder.gameSession().toBuilder()
                            .finishOrder(finishOrderWithCulo)
                            .build())
                    .build();
            final var roomWithRoles = this.playerRoleService.assignRoles(roomWithFinishOrder);

            return RegisterPlayerOutResult.builder()
                    .room(roomWithRoles)
                    .gameEnded(true)
                    .build();
        }

        return RegisterPlayerOutResult.builder()
                .room(roomWithFinishOrder)
                .gameEnded(false)
                .build();
    }
}
