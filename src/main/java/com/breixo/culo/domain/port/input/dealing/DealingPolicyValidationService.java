package com.breixo.culo.domain.port.input.dealing;

import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;

/** The Interface DealingPolicyValidationService. */
public interface DealingPolicyValidationService {

    /**
     * Validate dealing authority.
     *
     * @param room   the room
     * @param player the player
     */
    void validateDealingAuthority(Room room, Player player);
}
