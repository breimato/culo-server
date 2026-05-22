package com.breixo.culo.infrastructure.adapter.input.ws.room;

import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.infrastructure.config.CuloProperties;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The Class RoomAckCoordinator.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoomAckCoordinator {

  /** The culo properties. */
  private final CuloProperties culoProperties;

  /** The scheduler. */
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
 
    final var thread = new Thread(r, "room-ack-timeout");
    thread.setDaemon(true);
    return thread;
  });

  /** The pending by event id. */
  private final ConcurrentHashMap<String, PendingAck> pendingByEventId = new ConcurrentHashMap<>();

  /**
	 * Await all connected.
	 *
	 * @param room         the room
	 * @param continuation the continuation
	 * @return the string
	 */
  public String awaitAllConnected(final Room room, final Runnable continuation) {
 
    final var eventId = UUID.randomUUID().toString();
    final var expected = room.roomLobby().players().stream()
        .filter(Player::connected)
        .map(Player::id)
        .collect(Collectors.toUnmodifiableSet());

    if (expected.isEmpty()) {
      continuation.run();
      return eventId;
    }

    final var pending = new PendingAck(room.roomLobby().code(), expected, continuation);
    this.pendingByEventId.put(eventId, pending);

    final ScheduledFuture<?> timeout = this.scheduler.schedule(
        () -> this.complete(eventId, true),
        this.culoProperties.getRoom().getAckTimeoutMs(),
        TimeUnit.MILLISECONDS);
    pending.timeoutFuture = timeout;

    log.debug("ACK gate {} room {} waiting for {} player(s)", eventId, room.roomLobby().code(), expected.size());
    return eventId;
  }

  /**
	 * Record ack.
	 *
	 * @param roomCode the room code
	 * @param eventId  the event id
	 * @param playerId the player id
	 */
  public void recordAck(final String roomCode, final String eventId, final String playerId) {
 
    final var pending = this.pendingByEventId.get(eventId);
    if (Objects.isNull(pending) || BooleanUtils.isFalse(pending.roomCode.equals(roomCode))) {
      return;
    }

    synchronized (pending) {
      if (pending.completed) {
        return;
      }
      pending.received.add(playerId);
      if (pending.received.containsAll(pending.expectedPlayerIds)) {
        this.complete(eventId, false);
      }
    }
  }

  /**
	 * Complete.
	 *
	 * @param eventId  the event id
	 * @param timedOut the timed out
	 */
  private void complete(final String eventId, final boolean timedOut) {
 
    final var pending = this.pendingByEventId.remove(eventId);
    if (Objects.isNull(pending)) {
      return;
    }

    synchronized (pending) {
      if (pending.completed) {
        return;
      }
      pending.completed = true;
      if (Objects.nonNull(pending.timeoutFuture)) {
        pending.timeoutFuture.cancel(false);
      }
    }

    if (timedOut) {
      log.warn(
          "ACK timeout event {} room {} ({}/{} acks)",
          eventId,
          pending.roomCode,
          pending.received.size(),
          pending.expectedPlayerIds.size());
    } else {
      log.debug("ACK complete event {} room {}", eventId, pending.roomCode);
    }

    pending.continuation.run();
  }

  /**
	 * Shutdown.
	 */
  @PreDestroy
  void shutdown() {
    this.scheduler.shutdownNow();
  }

  /**
	 * The Class PendingAck.
	 */
  private static final class PendingAck {

    /** The room code. */
    private final String roomCode;
    
    /** The expected player ids. */
    private final Set<String> expectedPlayerIds;
    
    /** The received. */
    private final Set<String> received = ConcurrentHashMap.newKeySet();
    
    /** The continuation. */
    private final Runnable continuation;
    
    /** The timeout future. */
    private volatile ScheduledFuture<?> timeoutFuture;
    
    /** The completed. */
    private volatile boolean completed;

    /**
	 * Instantiates a new pending ack.
	 *
	 * @param roomCode          the room code
	 * @param expectedPlayerIds the expected player ids
	 * @param continuation      the continuation
	 */
    private PendingAck(
        final String roomCode,
        final Set<String> expectedPlayerIds,
        final Runnable continuation) {
      this.roomCode = roomCode;
      this.expectedPlayerIds = expectedPlayerIds;
      this.continuation = continuation;
    }
  }
}
