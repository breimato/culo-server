package com.breixo.culo.infrastructure.schedule;

import com.breixo.culo.domain.model.Room;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import com.breixo.culo.infrastructure.config.CuloProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomCleanupScheduler {

  /** The room persistence port. */
  private final RoomPersistencePort roomPersistencePort;

  /** The culo properties. */
  private final CuloProperties culoProperties;

  @Scheduled(fixedRate = 1_800_000)
  public void purgeInactiveRooms() {
    final var ttl = Duration.ofHours(this.culoProperties.getRoom().getTtlHours());
    final var cutoff = Instant.now().minus(ttl);
    this.roomPersistencePort.findAll().stream()
        .filter(room -> room.getLastActivity().isBefore(cutoff))
        .map(Room::getCode)
        .forEach(roomCode -> {
          this.roomPersistencePort.deleteByCode(roomCode);
          log.info("Sala {} eliminada por inactividad", roomCode);
        });
  }
}
