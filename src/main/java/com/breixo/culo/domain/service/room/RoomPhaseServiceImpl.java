package com.breixo.culo.domain.service.room;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

/**
 * The Class RoomPhaseServiceImpl.
 */
@Service
public class RoomPhaseServiceImpl implements RoomPhaseService {

    /** {@inheritDoc} */
    @Override
    public void requirePhase(final Room room, final GamePhase expectedPhase) {

        if (BooleanUtils.isFalse(room.roomLobby().phase().equals(expectedPhase))) {
            throw new GameException(GameExceptionConstants.WRONG_PHASE);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void requireLobbyPhase(final Room room) {

        if (BooleanUtils.isFalse(room.roomLobby().phase().equals(GamePhase.LOBBY))) {
            throw new RoomException(RoomExceptionConstants.GAME_ALREADY_STARTED);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Room withPhase(final Room room, final GamePhase gamePhase) {

        return room.toBuilder()
                .roomLobby(room.roomLobby().toBuilder()
                        .phase(gamePhase)
                        .build())
                .build();
    }
}
