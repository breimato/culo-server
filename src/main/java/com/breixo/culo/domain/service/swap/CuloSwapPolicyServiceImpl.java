package com.breixo.culo.domain.service.swap;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.swap.CuloSwapPolicyService;
import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * The Class CuloSwapPolicyServiceImpl.
 */
@Service
@RequiredArgsConstructor
public class CuloSwapPolicyServiceImpl implements CuloSwapPolicyService {

    /** The player lookup service. */
    private final PlayerLookupService playerLookupService;

    /** {@inheritDoc} */
    @Override
    public void validateInitiator(final Player player) {

        if (BooleanUtils.isFalse(PlayerRole.CULO.getId().equals(player.role().getId()))) {
            throw new GameException(GameExceptionConstants.NOT_CULO);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void validateNoActiveSwap(final Room room) {

        if (Objects.nonNull(room.culoSwapState().initiatorId())) {
            throw new GameException(GameExceptionConstants.SWAP_ALREADY_ACTIVE);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void validateNotAlreadyVoted(final Room room, final Player player) {

        if (room.culoSwapState().votes().containsKey(player.id())) {
            throw new GameException(GameExceptionConstants.SWAP_ALREADY_VOTED);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void validateTargetExists(final Room room, final String targetPlayerId) {

        this.playerLookupService.findPlayerById(room, targetPlayerId)
                .orElseThrow(() -> new RoomException(RoomExceptionConstants.PLAYER_NOT_IN_ROOM));
    }
}
