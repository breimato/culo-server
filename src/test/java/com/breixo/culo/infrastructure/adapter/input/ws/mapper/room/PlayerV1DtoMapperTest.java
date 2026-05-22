package com.breixo.culo.infrastructure.adapter.input.ws.mapper.room;

import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayerRoleV1Dto;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayerV1Dto;
import com.breixo.culo.infrastructure.mapper.player.PlayerRoleMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class PlayerV1DtoMapperTest.
 */
@ExtendWith(MockitoExtension.class)
class PlayerV1DtoMapperTest {

    /** The player V 1 dto mapper. */
    @InjectMocks
    PlayerV1DtoMapperImpl playerV1DtoMapper;

    /** The player role mapper. */
    @Mock
    PlayerRoleMapper playerRoleMapper;

    /**
	 * Test to player V 1 dto when player is valid then return mapped dto.
	 */
    @Test
    void testToPlayerV1Dto_whenPlayerIsValid_thenReturnMappedDto() {
        
        // Given
        final var player = Instancio.create(Player.class);
        final var playerRoleV1Dto = Instancio.create(PlayerRoleV1Dto.class);

        // When
        when(this.playerRoleMapper.toPlayerRoleV1Dto(player.role())).thenReturn(playerRoleV1Dto);
        final var playerV1Dto = this.playerV1DtoMapper.toPlayerV1Dto(player);

        // Then
        verify(this.playerRoleMapper, times(1)).toPlayerRoleV1Dto(player.role());
        assertEquals(player.id(), playerV1Dto.getId());
        assertEquals(player.nick(), playerV1Dto.getNick());
        assertEquals(player.connected(), playerV1Dto.getConnected());
        assertEquals(playerRoleV1Dto, playerV1Dto.getRole());
    }
}
