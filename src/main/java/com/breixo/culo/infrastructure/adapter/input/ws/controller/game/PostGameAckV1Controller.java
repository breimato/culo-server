package com.breixo.culo.infrastructure.adapter.input.ws.controller.game;

import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import com.breixo.culo.infrastructure.adapter.input.ws.RoomAckCoordinator;
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

    /**
     * The room persistence port.
     */
    private final RoomPersistencePort roomPersistencePort;

    /**
     * The room ack coordinator.
     */
    private final RoomAckCoordinator roomAckCoordinator;

    /**
     * {@inheritDoc}
     */
    @Override
    @MessageMapping("/game.ack")
    public ResponseEntity<Void> postGameAckV1(
            @Payload @Valid final PostGameAckV1RequestDto postGameAckV1RequestDto) {

        this.roomPersistencePort.findByCode(postGameAckV1RequestDto.getRoomCode())
                .flatMap(room -> room.findPlayerByClientId(postGameAckV1RequestDto.getClientId()))
                .ifPresent(player -> this.roomAckCoordinator.recordAck(
                        postGameAckV1RequestDto.getRoomCode(),
                        postGameAckV1RequestDto.getEventId(),
                        player.getId()));

        return ResponseEntity.noContent().build();
    }
}
