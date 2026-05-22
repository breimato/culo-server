package com.breixo.culo.domain.service.swap;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.swap.CuloSwapVoteCast;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.swap.CuloService;
import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Class CuloServiceImpl.
 */
@Service
@RequiredArgsConstructor
public class CuloServiceImpl implements CuloService {

    /** The player lookup service. */
    private final PlayerLookupService playerLookupService;

    /** {@inheritDoc} */
    @Override
    public CuloSwapVoteCast registerVote(
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

        final var voteCount = roomAfterVote.culoSwapState().votes().size();
        final var playerCount = roomAfterVote.roomLobby().players().size();
        final var allPlayersHaveVoted = voteCount == playerCount;

        return CuloSwapVoteCast.builder()
                .room(roomAfterVote)
                .allPlayersHaveVoted(allPlayersHaveVoted)
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSwapApproved(final Room room) {

        if (room.culoSwapState().votes().isEmpty()) {
            return false;
        }
        return room.culoSwapState().votes().values().stream().allMatch(Boolean.TRUE::equals);
    }

    /** {@inheritDoc} */
    @Override
    public Room applySwap(final Room room) {

        final var initiatorId = room.culoSwapState().initiatorId();
        final var targetId = room.culoSwapState().targetId();
        final var swappedHands = this.swapHands(room, initiatorId, targetId);
        final var updatedPlayers = this.swapRolesAfterApproval(room, initiatorId, targetId);

        return room.toBuilder()
                .roomLobby(room.roomLobby().toBuilder()
                        .players(updatedPlayers)
                        .build())
                .gameSession(room.gameSession().toBuilder()
                        .hands(swappedHands)
                        .lastCuloId(targetId)
                        .build())
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public Room clearSwap(final Room room) {

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

    /**
	 * Swap hands.
	 *
	 * @param room        the room
	 * @param initiatorId the initiator id
	 * @param targetId    the target id
	 * @return the hash map
	 */
    private HashMap<String, List<Card>> swapHands(
            final Room room,
            final String initiatorId,
            final String targetId) {

        final var initiatorHand = new ArrayList<>(room.gameSession().hands().getOrDefault(initiatorId, List.of()));
        final var targetHand = new ArrayList<>(room.gameSession().hands().getOrDefault(targetId, List.of()));
        final var hands = new HashMap<>(room.gameSession().hands());

        hands.put(initiatorId, List.copyOf(targetHand));
        hands.put(targetId, List.copyOf(initiatorHand));

        return hands;
    }

    /**
	 * Swap roles after approval.
	 *
	 * @param room        the room
	 * @param initiatorId the initiator id
	 * @param targetId    the target id
	 * @return the list
	 */
    private List<Player> swapRolesAfterApproval(
            final Room room,
            final String initiatorId,
            final String targetId) {

        final var target = this.playerLookupService.findPlayerById(room, targetId).orElseThrow();
        final var updatedPlayers = new ArrayList<Player>();

        for (final var player : room.roomLobby().players()) {
            updatedPlayers.add(this.applySwapRole(player, initiatorId, targetId, target));
        }

        return updatedPlayers;
    }

    /**
	 * Apply swap role.
	 *
	 * @param player      the player
	 * @param initiatorId the initiator id
	 * @param targetId    the target id
	 * @param target      the target
	 * @return the player
	 */
    private Player applySwapRole(
            final Player player,
            final String initiatorId,
            final String targetId,
            final Player target) {

        if (player.id().equals(initiatorId)) {
            return player.toBuilder().role(target.role()).build();
        }

        if (player.id().equals(targetId)) {
            return player.toBuilder().role(PlayerRole.CULO).build();
        }

        return player;
    }
}
