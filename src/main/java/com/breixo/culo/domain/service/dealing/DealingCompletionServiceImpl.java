package com.breixo.culo.domain.service.dealing;

import com.breixo.culo.domain.constants.GameConstants;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.dealing.CardDealingService;
import com.breixo.culo.domain.port.input.dealing.DealingCompletionService;
import com.breixo.culo.domain.port.input.player.PlayerRoleService;
import com.breixo.culo.domain.port.input.quad.QuadDiscardService;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/** The Class DealingCompletionServiceImpl. */
@Service
@RequiredArgsConstructor
public class DealingCompletionServiceImpl implements DealingCompletionService {

    /** The player role service. */
    private final PlayerRoleService playerRoleService;

    /** The card dealing service. */
    private final CardDealingService cardDealingService;

    /** The quad discard service. */
    private final QuadDiscardService quadDiscardService;

    /** The room phase service. */
    private final RoomPhaseService roomPhaseService;

    /** {@inheritDoc} */
    @Override
    public Room execute(final Room room) {

        final var rolesBeforeDeal = this.playerRoleService.captureExchangeRoles(room);
        final var needsExchange = this.playerRoleService.needsPostDealExchange(rolesBeforeDeal);
        final var dealtRoom = this.cardDealingService.dealCards(room);

        if (BooleanUtils.isTrue(needsExchange)) {
            return this.finishDealingWithExchange(dealtRoom, rolesBeforeDeal);
        }
        return this.finishDealingWithPlaying(dealtRoom);
    }

    /**
     * Finish dealing with exchange.
     *
     * @param room            the room
     * @param rolesBeforeDeal the roles before deal
     * @return the room
     */
    private Room finishDealingWithExchange(final Room room, final Map<PlayerRole, String> rolesBeforeDeal) {

        var roomWithRoles = this.playerRoleService.updatePlayerRoles(room, rolesBeforeDeal);
        final var culoId = rolesBeforeDeal.get(PlayerRole.CULO);
        final var ganadorId = rolesBeforeDeal.get(PlayerRole.GANADOR);
        roomWithRoles = this.cardDealingService.transferHighestCards(
                roomWithRoles,
                culoId,
                ganadorId,
                GameConstants.BEST_CARDS_FROM_CULO_TO_GANADOR);

        return this.roomPhaseService.withPhase(roomWithRoles, GamePhase.EXCHANGE);
    }

    /**
     * Finish dealing with playing.
     *
     * @param room the room
     * @return the room
     */
    private Room finishDealingWithPlaying(final Room room) {

        final var roomAfterQuadDiscard = this.quadDiscardService.discardQuadsForAllPlayers(room);
        return this.roomPhaseService.withPhase(roomAfterQuadDiscard, GamePhase.PLAYING);
    }
}
