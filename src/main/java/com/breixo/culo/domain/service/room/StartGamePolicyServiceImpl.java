package com.breixo.culo.domain.service.room;

import com.breixo.culo.domain.constants.RoomConstants;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.room.StartGamePolicyService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

/** The Class StartGamePolicyServiceImpl. */
@Service
public class StartGamePolicyServiceImpl implements StartGamePolicyService {

    /** {@inheritDoc} */
    @Override
    public void validateCanStart(final Room room, final Player player) {

        final var isHost = room.roomLobby().hostPlayerId().equals(player.id());

        if (BooleanUtils.isFalse(isHost)) {
            throw new RoomException(RoomExceptionConstants.NOT_HOST);
        }

        if (Integer.valueOf(room.roomLobby().players().size()).compareTo(RoomConstants.MIN_PLAYERS_TO_START) < 0) {
            throw new RoomException(RoomExceptionConstants.NOT_ENOUGH_PLAYERS);
        }
    }
}
