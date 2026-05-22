package com.breixo.culo.domain.service.cards;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.cards.DealPolicyService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/** The Class DealPolicyServiceImpl. */
@Service
public class DealPolicyServiceImpl implements DealPolicyService {

    /** {@inheritDoc} */
    @Override
    public void validateDealingAuthority(final Room room, final Player player) {

        final var isCulo = PlayerRole.CULO.getId().equals(player.role().getId());
        final var isFirstGame = Objects.isNull(room.gameSession().lastCuloId());
        final var isHost = room.roomLobby().hostPlayerId().equals(player.id());

        if (BooleanUtils.isFalse(isCulo) && BooleanUtils.isFalse(isFirstGame)) {
            throw new GameException(GameExceptionConstants.NOT_CULO);
        }

        if (BooleanUtils.isTrue(isFirstGame) && BooleanUtils.isFalse(isHost)) {
            throw new RoomException(RoomExceptionConstants.NOT_HOST);
        }
    }
}
