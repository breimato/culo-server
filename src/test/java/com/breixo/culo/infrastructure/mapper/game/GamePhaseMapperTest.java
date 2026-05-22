package com.breixo.culo.infrastructure.mapper.game;

import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.GamePhaseV1Dto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** The Class Game Phase Mapper Test. */
@ExtendWith(MockitoExtension.class)
class GamePhaseMapperTest {

    /** The game phase mapper. */
    @InjectMocks
    GamePhaseMapperImpl gamePhaseMapper;

    /** Test to game phase V 1 dto when phase is lobby then return lobby dto. */
    @Test
    void testToGamePhaseV1Dto_whenPhaseIsLobby_thenReturnLobbyDto() {
        // Given
        final var gamePhase = GamePhase.LOBBY;

        // When
        final var gamePhaseV1Dto = this.gamePhaseMapper.toGamePhaseV1Dto(gamePhase);

        // Then
        assertEquals(GamePhaseV1Dto.LOBBY, gamePhaseV1Dto);
    }

    /** Test to game phase when dto is null then return null. */
    @Test
    void testToGamePhase_whenDtoIsNull_thenReturnNull() {
        // When / Then
        assertNull(this.gamePhaseMapper.toGamePhase(null));
    }
}
