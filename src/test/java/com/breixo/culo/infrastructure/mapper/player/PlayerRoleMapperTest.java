package com.breixo.culo.infrastructure.mapper.player;

import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayerRoleV1Dto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** The Class Player Role Mapper Test. */
@ExtendWith(MockitoExtension.class)
class PlayerRoleMapperTest {

    /** The player role mapper. */
    @InjectMocks
    PlayerRoleMapperImpl playerRoleMapper;

    /** Test to player role V 1 dto when role is culo then return culo dto. */
    @Test
    void testToPlayerRoleV1Dto_whenRoleIsCulo_thenReturnCuloDto() {
        // Given
        final var playerRole = PlayerRole.CULO;

        // When
        final var playerRoleV1Dto = this.playerRoleMapper.toPlayerRoleV1Dto(playerRole);

        // Then
        assertEquals(PlayerRoleV1Dto.CULO, playerRoleV1Dto);
    }

    /** Test to player role when dto is null then return null. */
    @Test
    void testToPlayerRole_whenDtoIsNull_thenReturnNull() {
        // When / Then
        assertNull(this.playerRoleMapper.toPlayerRole(null));
    }
}
