package com.breixo.culo.domain.service.room;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.model.room.GameSessionContext;
import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.output.room.RoomRetrievalPersistencePort;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/** The Class GameContextServiceImpl. */
@Service
@RequiredArgsConstructor
public class GameContextServiceImpl implements GameContextService {

    /** The room retrieval persistence port. */
    private final RoomRetrievalPersistencePort roomRetrievalPersistencePort;

    /** The player lookup service. */
    private final PlayerLookupService playerLookupService;

    /** The room phase service. */
    private final RoomPhaseService roomPhaseService;

    /** {@inheritDoc} */
    @Override
    public GameSessionContext load(final String roomCode, final String clientId) {

        final var room = this.roomRetrievalPersistencePort.findByCode(roomCode)
                .orElseThrow(() -> new RoomException(RoomExceptionConstants.ROOM_NOT_FOUND));

        final var player = this.playerLookupService.findPlayerByClientId(room, clientId)
                .orElseThrow(() -> new RoomException(RoomExceptionConstants.PLAYER_NOT_IN_ROOM));

        return GameSessionContext.builder()
                .room(room)
                .player(player)
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public GameSessionContext loadWithPhase(
            final String roomCode,
            final String clientId,
            final GamePhase expectedPhase) {

        final var gameSessionContext = this.load(roomCode, clientId);
        this.roomPhaseService.requirePhase(gameSessionContext.room(), expectedPhase);

        return gameSessionContext;
    }

    /** {@inheritDoc} */
    @Override
    public String currentPlayerId(final Room room) {

        if (room.gameSession().playerOrder().isEmpty()) {
            return null;
        }

        return room.gameSession().playerOrder().get(room.gameSession().currentPlayerIndex());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPlayerOut(final Room room, final String playerId) {

        final var playerHand = room.gameSession().hands().get(playerId);

        return Objects.isNull(playerHand) || playerHand.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public List<String> activePlayerIds(final Room room) {

        return room.gameSession().playerOrder().stream()
                .filter(playerId -> BooleanUtils.isFalse(this.isPlayerOut(room, playerId)))
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    public void requirePlayerTurn(final Room room, final Player player) {

        final var currentPlayerId = this.currentPlayerId(room);

        if (BooleanUtils.isFalse(player.id().equals(currentPlayerId))) {
            throw new GameException(GameExceptionConstants.NOT_YOUR_TURN);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void requirePlayerHasCards(final Room room, final Player player) {

        if (this.isPlayerOut(room, player.id())) {
            throw new GameException(GameExceptionConstants.PLAYER_OUT);
        }
    }
}
