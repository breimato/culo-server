package com.breixo.culo.infrastructure.adapter.input.ws.support.game;

import com.breixo.culo.domain.model.game.PlayResult;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.cards.QuadDiscardService;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.output.room.RoomRetrievalPersistencePort;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomEventPublisher;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

/**
 * The Class PlayFollowUpSupport.
 */
@Component
@RequiredArgsConstructor
public class PlayFollowUpSupport {

    /** The room event publisher. */
    private final RoomEventPublisher roomEventPublisher;

    /** The room retrieval persistence port. */
    private final RoomRetrievalPersistencePort roomRetrievalPersistencePort;

    /** The room save persistence port. */
    private final RoomSavePersistencePort roomSavePersistencePort;

    /** The quad discard service. */
    private final QuadDiscardService quadDiscardService;

    /** The game session context service. */
    private final GameContextService gameSessionContextService;

    /**
	 * Play follow up task.
	 *
	 * @param roomCode   the room code
	 * @param playResult the play result
	 * @return the runnable
	 */
    public Runnable playFollowUpTask(final String roomCode, final PlayResult playResult) {
        return () -> this.publishPlayFollowUp(roomCode, playResult);
    }

    /**
	 * Publish play follow up.
	 *
	 * @param roomCode   the room code
	 * @param playResult the play result
	 */
    public void publishPlayFollowUp(final String roomCode, final PlayResult playResult) {
        final var room = this.roomRetrievalPersistencePort.findByCode(roomCode).orElse(playResult.room());
        this.roomEventPublisher.publishRoomState(room);
        final var roomAfterDrain = this.publishPendingQuadDiscards(room);
        if (BooleanUtils.isTrue(playResult.gameFinished())) {
            this.roomEventPublisher.publishGameEnded(roomAfterDrain);
            this.roomEventPublisher.publishAllHands(roomAfterDrain);
        } else {
            this.roomEventPublisher.publishHandUpdate(roomAfterDrain, playResult.playerId());

            if (BooleanUtils.isTrue(playResult.roundClosed())) {
                final var currentPlayerId = this.gameSessionContextService.currentPlayerId(roomAfterDrain);
                this.roomEventPublisher.publishRoundEnded(roomAfterDrain, currentPlayerId);
            }

            this.roomEventPublisher.publishTurnChanged(roomAfterDrain);
        }
    }

    /**
	 * Publish pending quad discards.
	 *
	 * @param room the room
	 * @return the room
	 */
    public Room publishPendingQuadDiscards(final Room room) {
        final var discardQuadsResult = this.quadDiscardService.drainQuadDiscards(room);
        final var drainedRoom = this.roomSavePersistencePort.save(discardQuadsResult.room());
        discardQuadsResult.events().forEach(quadDiscardEvent -> {
            this.roomEventPublisher.publishQuadDiscarded(drainedRoom, quadDiscardEvent);
            this.roomEventPublisher.publishHandUpdate(drainedRoom, quadDiscardEvent.playerId());
        });
        return drainedRoom;
    }

    /**
	 * Publish pass follow up.
	 *
	 * @param room        the room
	 * @param roundClosed the round closed
	 */
    public void publishPassFollowUp(final Room room, final boolean roundClosed) {
        this.roomEventPublisher.publishRoomState(room);
        final var roomAfterDrain = this.publishPendingQuadDiscards(room);
        if (BooleanUtils.isTrue(roundClosed)) {
            final var currentPlayerId = this.gameSessionContextService.currentPlayerId(roomAfterDrain);
            this.roomEventPublisher.publishRoundEnded(roomAfterDrain, currentPlayerId);
        }
        this.roomEventPublisher.publishTurnChanged(roomAfterDrain);
    }

    /**
	 * Publish exchange follow up.
	 *
	 * @param room the room
	 */
    public void publishExchangeFollowUp(final Room room) {
        this.roomEventPublisher.publishRoomState(room);
        final var roomAfterDrain = this.publishPendingQuadDiscards(room);
        roomAfterDrain.roomLobby().players().forEach(player ->
                this.roomEventPublisher.publishHandUpdate(roomAfterDrain, player.id()));
    }
}
