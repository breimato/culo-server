package com.breixo.culo.domain.service.room;

import com.breixo.culo.domain.command.room.CreateRoomCommand;
import com.breixo.culo.domain.model.game.Round;
import com.breixo.culo.domain.model.room.CuloSwapState;
import com.breixo.culo.domain.model.room.ExchangeState;
import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.room.RoomFactoryService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** The Class RoomFactoryServiceImpl. */
@Service
public class RoomFactoryServiceImpl implements RoomFactoryService {

    /** {@inheritDoc} */
    @Override
    public Room createEmptyRoom(
            final CreateRoomCommand createRoomCommand,
            final String roomCode,
            final String hostPlayerId) {

        return Room.builder()
                .roomLobby(RoomLobby.builder()
                        .code(roomCode)
                        .hostPlayerId(hostPlayerId)
                        .players(List.of())
                        .phase(GamePhase.LOBBY)
                        .lastActivity(Instant.now())
                        .build())
                .gameSession(GameSession.builder()
                        .hands(Map.of())
                        .playerOrder(List.of())
                        .currentPlayerIndex(0)
                        .currentRound(Round.builder()
                                .requirement(0)
                                .lastCardNumber(0)
                                .playersPassedSinceLastPlay(Set.of())
                                .lastPlayedCards(List.of())
                                .build())
                        .playEpoch(0)
                        .finishOrder(List.of())
                        .pendingQuadDiscards(List.of())
                        .build())
                .exchangeState(ExchangeState.builder()
                        .pendingGanadorToCulo(List.of())
                        .pendingSubcampeonToPenultimo(List.of())
                        .exchangeDone(Set.of())
                        .build())
                .culoSwapState(CuloSwapState.builder()
                        .votes(Map.of())
                        .build())
                .build();
    }
}
