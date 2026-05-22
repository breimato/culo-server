package com.breixo.culo.domain.service.room;

import com.breixo.culo.domain.model.room.CuloSwapState;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.game.TurnManagementService;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.input.room.PlayerRemovalFromRoomService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** The Class PlayerRemovalFromRoomServiceImpl. */
@Service
@RequiredArgsConstructor
public class PlayerRemovalFromRoomServiceImpl implements PlayerRemovalFromRoomService {

    /** The game context service. */
    private final GameContextService gameContextService;

    /** The turn management service. */
    private final TurnManagementService turnManagementService;

    /** {@inheritDoc} */
    @Override
    public Room removePlayerFromGameState(final Room room, final String playerId) {

        final var roomWithoutPlayer = this.buildRoomWithoutPlayer(room, playerId);

        if (roomWithoutPlayer.gameSession().playerOrder().isEmpty()) {
            return roomWithoutPlayer;
        }

        return this.realignTurnAfterRemoval(room, roomWithoutPlayer, playerId);
    }

    private Room buildRoomWithoutPlayer(final Room room, final String playerId) {

        final var handsWithoutPlayer = new HashMap<>(room.gameSession().hands());
        handsWithoutPlayer.remove(playerId);

        final var playerOrderWithoutPlayer = this.withoutPlayer(room.gameSession().playerOrder(), playerId);
        final var finishOrderWithoutPlayer = this.withoutPlayer(room.gameSession().finishOrder(), playerId);
        final var exchangeDoneWithoutPlayer = this.withoutPlayer(room.exchangeState().exchangeDone(), playerId);

        return room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .hands(handsWithoutPlayer)
                        .playerOrder(playerOrderWithoutPlayer)
                        .finishOrder(finishOrderWithoutPlayer)
                        .build())
                .exchangeState(room.exchangeState().toBuilder()
                        .exchangeDone(exchangeDoneWithoutPlayer)
                        .build())
                .culoSwapState(this.clearCuloSwapIfInvolved(room.culoSwapState(), playerId))
                .build();
    }

    private Room realignTurnAfterRemoval(
            final Room roomBeforeRemoval,
            final Room roomWithoutPlayer,
            final String playerId) {

        if (BooleanUtils.isTrue(playerId.equals(this.gameContextService.currentPlayerId(roomBeforeRemoval)))) {
            final var firstActiveIndex = this.turnManagementService.findFirstActivePlayerIndex(roomWithoutPlayer);
            return this.turnManagementService.ensureCurrentPlayerIsActive(
                    this.applyCurrentPlayerIndex(roomWithoutPlayer, firstActiveIndex));
        }

        final var currentPlayerId = this.gameContextService.currentPlayerId(roomBeforeRemoval);

        if (Objects.isNull(currentPlayerId)) {
            return this.turnManagementService.ensureCurrentPlayerIsActive(roomWithoutPlayer);
        }

        final var newIndex = roomWithoutPlayer.gameSession().playerOrder().indexOf(currentPlayerId);

        if (newIndex < 0) {
            return this.turnManagementService.ensureCurrentPlayerIsActive(roomWithoutPlayer);
        }

        return this.turnManagementService.ensureCurrentPlayerIsActive(
                this.applyCurrentPlayerIndex(roomWithoutPlayer, newIndex));
    }

    private List<String> withoutPlayer(final List<String> playerIds, final String playerId) {

        return playerIds.stream()
                .filter(id -> BooleanUtils.isFalse(id.equals(playerId)))
                .toList();
    }

    private Set<String> withoutPlayer(final Set<String> playerIds, final String playerId) {

        return playerIds.stream()
                .filter(id -> BooleanUtils.isFalse(id.equals(playerId)))
                .collect(Collectors.toUnmodifiableSet());
    }

    private CuloSwapState clearCuloSwapIfInvolved(final CuloSwapState culoSwapState, final String playerId) {

        if (playerId.equals(culoSwapState.initiatorId()) || playerId.equals(culoSwapState.targetId())) {
            return CuloSwapState.builder()
                    .votes(Map.of())
                    .build();
        }

        final var votesWithoutPlayer = new HashMap<>(culoSwapState.votes());
        votesWithoutPlayer.remove(playerId);

        return culoSwapState.toBuilder()
                .votes(votesWithoutPlayer)
                .build();
    }

    private Room applyCurrentPlayerIndex(final Room room, final Integer playerIndex) {

        return room.toBuilder()
                .gameSession(room.gameSession().toBuilder()
                        .currentPlayerIndex(playerIndex)
                        .build())
                .build();
    }
}
