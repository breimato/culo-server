package com.breixo.culo.infrastructure.adapter.input.ws.mapper.room;

import com.breixo.culo.domain.model.cards.enums.CardRank;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardRankNameV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayerV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.RoomStateV1ResponseDto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.cards.CardV1DtoMapper;
import com.breixo.culo.infrastructure.mapper.game.GamePhaseMapper;
import com.breixo.culo.infrastructure.mapper.player.PlayerRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * The Class RoomStateV1ResponseMapper.
 */
@Component
@RequiredArgsConstructor
public class RoomStateV1ResponseMapper {

    /** The game phase mapper. */
    private final GamePhaseMapper gamePhaseMapper;

    /** The player role mapper. */
    private final PlayerRoleMapper playerRoleMapper;

    /** The card V 1 dto mapper. */
    private final CardV1DtoMapper cardV1DtoMapper;

    /**
     * To room state V 1 response dto.
     *
     * @param room the room
     * @return the room state V 1 response dto
     */
    public RoomStateV1ResponseDto toRoomStateV1ResponseDto(final Room room) {
        final var round = room.gameSession().currentRound();
        final var lastRankName = this.toCardRankNameV1Dto(round.lastRank());
        final var players = room.roomLobby().players().stream()
                .map(player -> this.toPlayerV1Dto(player, room))
                .toList();

        final String currentPlayerId;

        if (room.gameSession().playerOrder().isEmpty()) {
            currentPlayerId = null;
        } else {
            currentPlayerId = room.gameSession().playerOrder().get(room.gameSession().currentPlayerIndex());
        }

        return RoomStateV1ResponseDto.builder()
                .roomCode(room.roomLobby().code())
                .hostPlayerId(room.roomLobby().hostPlayerId())
                .phase(this.gamePhaseMapper.toGamePhaseV1Dto(room.roomLobby().phase()))
                .players(players)
                .currentPlayerId(currentPlayerId)
                .roundRequirement(round.requirement())
                .lastRankName(lastRankName)
                .lastPlayedCards(this.cardV1DtoMapper.toCardV1DtoList(round.lastPlayedCards()))
                .culoSwapInitiatorId(room.culoSwapState().initiatorId())
                .culoSwapTargetId(room.culoSwapState().targetId())
                .exchangeDonePlayerIds(List.copyOf(room.exchangeState().exchangeDone()))
                .playEpoch(room.gameSession().playEpoch())
                .build();
    }

    private PlayerV1Dto toPlayerV1Dto(final Player player, final Room room) {
        final var hand = room.gameSession().hands().getOrDefault(player.id(), List.of());
        return PlayerV1Dto.builder()
                .id(player.id())
                .nick(player.nick())
                .connected(player.connected())
                .role(this.playerRoleMapper.toPlayerRoleV1Dto(player.role()))
                .cardCount(hand.size())
                .build();
    }

    private CardRankNameV1Dto toCardRankNameV1Dto(final CardRank cardRank) {
        if (Objects.isNull(cardRank)) {
            return null;
        }
        return CardRankNameV1Dto.valueOf(cardRank.name());
    }
}
