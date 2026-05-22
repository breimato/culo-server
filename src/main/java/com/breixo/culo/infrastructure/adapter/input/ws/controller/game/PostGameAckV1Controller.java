package com.breixo.culo.infrastructure.adapter.input.ws.controller.game;

import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import com.breixo.culo.domain.port.output.room.RoomRetrievalPersistencePort;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomAckCoordinator;
import com.breixo.culo.infrastructure.adapter.input.ws.api.PostGameAckV1Api;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostGameAckV1RequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * The Class PostGameAckV1Controller.
 */
@Controller
@RequiredArgsConstructor
public class PostGameAckV1Controller implements PostGameAckV1Api {

    /** The room retrieval persistence port. */
    private final RoomRetrievalPersistencePort roomRetrievalPersistencePort;

    /** The room ack coordinator. */
    private final RoomAckCoordinator roomAckCoordinator;

    /** The player lookup service. */
    private final PlayerLookupService playerLookupService;

    /**
     * {@inheritDoc}
     */
    @Override
    @MessageMapping("/game.ack")
    public ResponseEntity<Void> postGameAckV1(
            @Payload @Valid final PostGameAckV1RequestDto postGameAckV1RequestDto) {

        this.roomRetrievalPersistencePort.findByCode(postGameAckV1RequestDto.getRoomCode())
                .flatMap(room -> this.playerLookupService.findPlayerByClientId(room, postGameAckV1RequestDto.getClientId()))
                .ifPresent(player -> this.roomAckCoordinator.recordAck(
                        postGameAckV1RequestDto.getRoomCode(),
                        postGameAckV1RequestDto.getEventId(),
                        player.id()));

        return ResponseEntity.noContent().build();
    }
}
