package com.breixo.culo.infrastructure.adapter.input.ws.support;

import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.model.game.PlayResult;
import com.breixo.culo.domain.port.output.room.RoomRetrievalPersistencePort;
import com.breixo.culo.infrastructure.adapter.input.ws.RoomEventPublisher;
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
    this.publishPendingQuadDiscards(room);
    if (BooleanUtils.isTrue(playResult.gameEnded())) {
      this.roomEventPublisher.publishGameEnded(room);
      this.roomEventPublisher.publishAllHands(room);
    } else {
      this.roomEventPublisher.publishHandUpdate(room, playResult.playerId());

      if (BooleanUtils.isTrue(playResult.roundEnded())) {
        this.roomEventPublisher.publishRoundEnded(room, room.getCurrentPlayerId());
      }

      this.roomEventPublisher.publishTurnChanged(room);
    }
  }

  /**
	 * Publish pending quad discards.
	 *
	 * @param room the room
	 */
  public void publishPendingQuadDiscards(final Room room) {
    room.drainQuadDiscards().forEach(quadDiscardEvent -> {
      this.roomEventPublisher.publishQuadDiscarded(room, quadDiscardEvent);
      this.roomEventPublisher.publishHandUpdate(room, quadDiscardEvent.playerId());
    });
  }

  /**
	 * Publish pass follow up.
	 *
	 * @param room       the room
	 * @param roundEnded the round ended
	 */
  public void publishPassFollowUp(final Room room, final boolean roundEnded) {
    this.roomEventPublisher.publishRoomState(room);
    this.publishPendingQuadDiscards(room);
    if (BooleanUtils.isTrue(roundEnded)) {
      this.roomEventPublisher.publishRoundEnded(room, room.getCurrentPlayerId());
    }
    this.roomEventPublisher.publishTurnChanged(room);
  }

  /**
	 * Publish exchange follow up.
	 *
	 * @param room the room
	 */
  public void publishExchangeFollowUp(final Room room) {
    this.roomEventPublisher.publishRoomState(room);
    this.publishPendingQuadDiscards(room);
    room.getPlayers().forEach(player ->
        this.roomEventPublisher.publishHandUpdate(room, player.getId()));
  }
}
