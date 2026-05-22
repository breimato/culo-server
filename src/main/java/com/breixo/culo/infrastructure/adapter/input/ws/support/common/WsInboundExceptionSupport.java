package com.breixo.culo.infrastructure.adapter.input.ws.support.common;

import com.breixo.culo.domain.exception.CuloException;
import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import com.breixo.culo.domain.port.output.room.RoomRetrievalPersistencePort;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * The Class WsInboundExceptionSupport.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WsInboundExceptionSupport {

  /** The room event publisher. */
  private final RoomEventPublisher roomEventPublisher;

  /** The room retrieval persistence port. */
  private final RoomRetrievalPersistencePort roomRetrievalPersistencePort;

  /** The player lookup service. */
  private final PlayerLookupService playerLookupService;

  /**
	 * Publish error to client.
	 *
	 * @param clientId      the client id
	 * @param culoException the culo exception
	 */
  public void publishErrorToClient(final String clientId, final CuloException culoException) {
    log.warn("WS inbound error for client {}: {}", clientId, culoException.getMessage());
    this.roomEventPublisher.publishErrorToClient(clientId, culoException);
  }

  /**
	 * Publish error to client and player.
	 *
	 * @param clientId      the client id
	 * @param roomCode      the room code
	 * @param culoException the culo exception
	 */
  public void publishErrorToClientAndPlayer(
      final String clientId,
      final String roomCode,
      final CuloException culoException) {
    this.publishErrorToClient(clientId, culoException);
    final var playerId = this.resolvePlayerId(clientId, roomCode);
    if (playerId.isPresent()) {
      this.roomEventPublisher.publishError(playerId.get(), culoException);
    }
  }

  /**
	 * Resolve player id.
	 *
	 * @param clientId the client id
	 * @param roomCode the room code
	 * @return the optional
	 */
  public Optional<String> resolvePlayerId(final String clientId, final String roomCode) {
    if (StringUtils.isBlank(roomCode)) {
      return Optional.empty();
    }
    return this.roomRetrievalPersistencePort.findByCode(roomCode)
        .flatMap(room -> this.playerLookupService.findPlayerByClientId(room, clientId))
        .map(player -> player.id());
  }
}
