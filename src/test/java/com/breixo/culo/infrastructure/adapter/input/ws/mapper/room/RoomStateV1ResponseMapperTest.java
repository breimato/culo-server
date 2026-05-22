package com.breixo.culo.infrastructure.adapter.input.ws.mapper.room;

import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.cards.enums.CardRank;
import com.breixo.culo.domain.model.game.Round;
import com.breixo.culo.domain.model.room.GameSession;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.RoomLobby;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardRankNameV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CardV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.GamePhaseV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.mapper.cards.CardV1DtoMapper;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayerRoleV1Dto;
import com.breixo.culo.infrastructure.mapper.game.GamePhaseMapper;
import com.breixo.culo.infrastructure.mapper.player.PlayerRoleMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class RoomStateV1ResponseMapperTest.
 */
@ExtendWith(MockitoExtension.class)
class RoomStateV1ResponseMapperTest {

    /** The room state V 1 response mapper. */
    @InjectMocks
    RoomStateV1ResponseMapper roomStateV1ResponseMapper;

    /** The game phase mapper. */
    @Mock
    GamePhaseMapper gamePhaseMapper;

    /** The player role mapper. */
    @Mock
    PlayerRoleMapper playerRoleMapper;

    /** The card V 1 dto mapper. */
    @Mock
    CardV1DtoMapper cardV1DtoMapper;

    /**
	 * Test to room state V 1 response dto when player order exists then map current
	 * player id.
	 */
    @Test
    void testToRoomStateV1ResponseDto_whenPlayerOrderExists_thenMapCurrentPlayerId() {
        
        // Given
        final var player = Instancio.of(Player.class)
                .set(field(Player::id), "player-1")
                .create();
        final var lastPlayedCards = List.of(Instancio.create(Card.class));
        final var currentRound = Instancio.of(Round.class)
                .set(field(Round::lastRank), CardRank.SIETE)
                .set(field(Round::requirement), 2)
                .set(field(Round::lastPlayedCards), lastPlayedCards)
                .create();
        final var hands = new HashMap<String, List<Card>>();
        hands.put("player-1", List.of(Instancio.create(Card.class), Instancio.create(Card.class)));
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::playerOrder), List.of("player-1", "player-2"))
                .set(field(GameSession::currentPlayerIndex), 0)
                .set(field(GameSession::currentRound), currentRound)
                .set(field(GameSession::hands), hands)
                .set(field(GameSession::playEpoch), 3)
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::code), "ABCD")
                .set(field(RoomLobby::hostPlayerId), "host-1")
                .set(field(RoomLobby::phase), GamePhase.PLAYING)
                .set(field(RoomLobby::players), List.of(player))
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .set(field(Room::gameSession), gameSession)
                .create();
        final var gamePhaseV1Dto = GamePhaseV1Dto.PLAYING;
        final var playerRoleV1Dto = Instancio.create(PlayerRoleV1Dto.class);
        final var cardV1DtoList = List.of(Instancio.create(CardV1Dto.class));

        // When
        when(this.gamePhaseMapper.toGamePhaseV1Dto(GamePhase.PLAYING)).thenReturn(gamePhaseV1Dto);
        when(this.playerRoleMapper.toPlayerRoleV1Dto(player.role())).thenReturn(playerRoleV1Dto);
        when(this.cardV1DtoMapper.toCardV1DtoList(lastPlayedCards)).thenReturn(cardV1DtoList);
        final var roomStateV1ResponseDto = this.roomStateV1ResponseMapper.toRoomStateV1ResponseDto(room);

        // Then
        verify(this.gamePhaseMapper, times(1)).toGamePhaseV1Dto(GamePhase.PLAYING);
        verify(this.playerRoleMapper, times(1)).toPlayerRoleV1Dto(player.role());
        verify(this.cardV1DtoMapper, times(1)).toCardV1DtoList(lastPlayedCards);
        assertEquals("ABCD", roomStateV1ResponseDto.getRoomCode());
        assertEquals("host-1", roomStateV1ResponseDto.getHostPlayerId());
        assertEquals(gamePhaseV1Dto, roomStateV1ResponseDto.getPhase());
        assertEquals("player-1", roomStateV1ResponseDto.getCurrentPlayerId());
        assertEquals(CardRankNameV1Dto.SIETE, roomStateV1ResponseDto.getLastRankName());
        assertEquals(2, roomStateV1ResponseDto.getRoundRequirement());
        assertEquals(cardV1DtoList, roomStateV1ResponseDto.getLastPlayedCards());
        assertEquals(3, roomStateV1ResponseDto.getPlayEpoch());
        assertEquals(1, roomStateV1ResponseDto.getPlayers().size());
        assertEquals(2, roomStateV1ResponseDto.getPlayers().getFirst().getCardCount());
    }

    /**
	 * Test to room state V 1 response dto when player order empty then current
	 * player id is null.
	 */
    @Test
    void testToRoomStateV1ResponseDto_whenPlayerOrderEmpty_thenCurrentPlayerIdIsNull() {
        
        // Given
        final var currentRound = Instancio.of(Round.class)
                .set(field(Round::lastRank), null)
                .set(field(Round::lastPlayedCards), List.<Card>of())
                .create();
        final var gameSession = Instancio.of(GameSession.class)
                .set(field(GameSession::playerOrder), List.<String>of())
                .set(field(GameSession::currentRound), currentRound)
                .set(field(GameSession::hands), Map.<String, List<Card>>of())
                .create();
        final var roomLobby = Instancio.of(RoomLobby.class)
                .set(field(RoomLobby::players), List.<Player>of())
                .set(field(RoomLobby::phase), GamePhase.LOBBY)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::roomLobby), roomLobby)
                .set(field(Room::gameSession), gameSession)
                .create();
        final var gamePhaseV1Dto = GamePhaseV1Dto.LOBBY;

        // When
        when(this.gamePhaseMapper.toGamePhaseV1Dto(GamePhase.LOBBY)).thenReturn(gamePhaseV1Dto);
        when(this.cardV1DtoMapper.toCardV1DtoList(List.of())).thenReturn(List.of());
        final var roomStateV1ResponseDto = this.roomStateV1ResponseMapper.toRoomStateV1ResponseDto(room);

        // Then
        verify(this.gamePhaseMapper, times(1)).toGamePhaseV1Dto(GamePhase.LOBBY);
        verify(this.cardV1DtoMapper, times(1)).toCardV1DtoList(List.of());
        assertNull(roomStateV1ResponseDto.getCurrentPlayerId());
        assertNull(roomStateV1ResponseDto.getLastRankName());
    }
}
