package com.breixo.culo.domain.service.room;

import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** The Class PlayerLookupServiceImpl. */
@Service
public class PlayerLookupServiceImpl implements PlayerLookupService {

    /** {@inheritDoc} */
    @Override
    public Optional<Player> findPlayerByClientId(final Room room, final String clientId) {

        return room.roomLobby().players().stream()
                .filter(player -> player.clientId().equals(clientId))
                .findFirst();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Player> findPlayerById(final Room room, final String playerId) {

        return room.roomLobby().players().stream()
                .filter(player -> player.id().equals(playerId))
                .findFirst();
    }
}
