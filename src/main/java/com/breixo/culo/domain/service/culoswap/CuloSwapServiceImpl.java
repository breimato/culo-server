package com.breixo.culo.domain.service.culoswap;

import com.breixo.culo.domain.model.culoswap.CuloSwapVoteRegistrationResult;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.culoswap.CuloSwapService;
import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** The Class CuloSwapServiceImpl. */
@Service
@RequiredArgsConstructor
public class CuloSwapServiceImpl implements CuloSwapService {

    /** The player lookup service. */
    private final PlayerLookupService playerLookupService;

    /** {@inheritDoc} */
    @Override
    public CuloSwapVoteRegistrationResult registerCuloSwapVote(
            final Room room,
            final String playerId,
            final boolean accept) {

        final var votes = new HashMap<>(room.culoSwapState().votes());

        votes.put(playerId, accept);

        final var roomAfterVote = room.toBuilder()
                .culoSwapState(room.culoSwapState().toBuilder()
                        .votes(votes)
                        .build())
                .build();

        final var allVoted = Integer.valueOf(roomAfterVote.culoSwapState().votes().size())
                .equals(roomAfterVote.roomLobby().players().size());

        return CuloSwapVoteRegistrationResult.builder()
                .room(roomAfterVote)
                .allVoted(allVoted)
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCuloSwapApproved(final Room room) {

        if (room.culoSwapState().votes().isEmpty()) {
            return false;
        }
        return room.culoSwapState().votes().values().stream().allMatch(Boolean.TRUE::equals);
    }

    /** {@inheritDoc} */
    @Override
    public Room applyCuloSwap(final Room room) {

        final var initiatorId = room.culoSwapState().initiatorId();
        final var targetId = room.culoSwapState().targetId();
        final var target = this.playerLookupService.findPlayerById(room, targetId).orElseThrow();
        final var initiatorHand = new ArrayList<>(room.gameSession().hands().getOrDefault(initiatorId, List.of()));
        final var targetHand = new ArrayList<>(room.gameSession().hands().getOrDefault(targetId, List.of()));
        final var hands = new HashMap<>(room.gameSession().hands());
        hands.put(initiatorId, List.copyOf(targetHand));
        hands.put(targetId, List.copyOf(initiatorHand));

        final var updatedPlayers = room.roomLobby().players().stream()
                .map(player -> {
                    if (player.id().equals(initiatorId)) {
                        return player.toBuilder().role(target.role()).build();
                    }
                    if (player.id().equals(targetId)) {
                        return player.toBuilder().role(PlayerRole.CULO).build();
                    }
                    return player;
                })
                .toList();

        return room.toBuilder()
                .roomLobby(room.roomLobby().toBuilder()
                        .players(updatedPlayers)
                        .build())
                .gameSession(room.gameSession().toBuilder()
                        .hands(hands)
                        .lastCuloId(targetId)
                        .build())
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public Room clearCuloSwap(final Room room) {

        return room.toBuilder()
                .culoSwapState(room.culoSwapState().toBuilder()
                        .initiatorId(null)
                        .targetId(null)
                        .votes(new HashMap<>())
                        .build())
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public Room initiateSwap(final Room room, final String initiatorId, final String targetPlayerId) {

        return room.toBuilder()
                .culoSwapState(room.culoSwapState().toBuilder()
                        .initiatorId(initiatorId)
                        .targetId(targetPlayerId)
                        .build())
                .build();
    }
}
