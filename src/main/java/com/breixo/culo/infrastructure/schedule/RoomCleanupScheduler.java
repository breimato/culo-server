package com.breixo.culo.infrastructure.schedule;

import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.output.room.RoomDeletionPersistencePort;
import com.breixo.culo.domain.port.output.room.RoomRetrievalPersistencePort;
import com.breixo.culo.infrastructure.config.CuloProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * The Class RoomCleanupScheduler.
 */
@Component
@RequiredArgsConstructor
public class RoomCleanupScheduler {

  /** The room retrieval persistence port. */
  private final RoomRetrievalPersistencePort roomRetrievalPersistencePort;
  
  /** The room deletion persistence port. */
  private final RoomDeletionPersistencePort roomDeletionPersistencePort;
  
  /** The culo properties. */
  private final CuloProperties culoProperties;

  /**
	 * Purge inactive rooms.
	 */
  @Scheduled(fixedRate = 1_800_000)
  public void purgeInactiveRooms() {
    final var ttl = Duration.ofHours(this.culoProperties.getRoom().getTtlHours());
    final var cutoff = Instant.now().minus(ttl);

    this.roomRetrievalPersistencePort.findAll().stream()
        .filter(room -> room.roomLobby().lastActivity().isBefore(cutoff))
        .map(room -> room.roomLobby().code())
        .forEach(this.roomDeletionPersistencePort::deleteByCode);
  }
}
