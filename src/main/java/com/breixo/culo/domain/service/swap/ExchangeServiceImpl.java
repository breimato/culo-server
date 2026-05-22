package com.breixo.culo.domain.service.swap;

import com.breixo.culo.domain.command.swap.ExchangeGiveCommand;
import com.breixo.culo.domain.constants.GameConstants;
import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.swap.ExchangeService;
import com.breixo.culo.domain.port.input.game.PlayBuilderService;
import com.breixo.culo.domain.port.input.player.HandManagementService;
import com.breixo.culo.domain.port.input.player.PlayerRoleService;
import com.breixo.culo.domain.port.input.cards.QuadDiscardService;
import com.breixo.culo.domain.port.input.room.PlayerLookupService;
import com.breixo.culo.domain.port.input.room.RoomPhaseService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

/**
 * The Class ExchangeServiceImpl.
 */
@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    /** The play builder service. */
    private final PlayBuilderService playBuilderService;

    /** The hand management service. */
    private final HandManagementService handManagementService;

    /** The player role service. */
    private final PlayerRoleService playerRoleService;

    /** The quad discard service. */
    private final QuadDiscardService quadDiscardService;

    /** The room phase service. */
    private final RoomPhaseService roomPhaseService;

    /** The player lookup service. */
    private final PlayerLookupService playerLookupService;

    /** {@inheritDoc} */
    @Override
    public Room processGive(final Room room, final String playerId, final ExchangeGiveCommand exchangeGiveCommand) {

        final var cards = this.playBuilderService.toCards(exchangeGiveCommand.cards(), room, playerId);
        final var player = this.playerLookupService.findPlayerById(room, playerId).orElseThrow();
        final var role = player.role();

        if (PlayerRole.GANADOR.getId().equals(role.getId())) {
            return this.processGanadorGive(room, playerId, cards);
        }
        if (PlayerRole.SUBCAMPEON.getId().equals(role.getId())) {
            return this.processSubcampeonGive(room, playerId, cards);
        }
        return room;
    }

    /** {@inheritDoc} */
    @Override
    public Room finalizeIfComplete(final Room room) {

        if (BooleanUtils.isFalse(this.isExchangeComplete(room))) {
            return room;
        }

        final var roomAfterReset = this.playerRoleService.resetPlayerRoles(room);
        final var roomAfterQuads = this.quadDiscardService.discardQuadsForAllPlayers(roomAfterReset);
        return this.roomPhaseService.withPhase(roomAfterQuads, GamePhase.PLAYING);
    }

    /**
	 * Process ganador give.
	 *
	 * @param room      the room
	 * @param ganadorId the ganador id
	 * @param cards     the cards
	 * @return the room
	 */
    private Room processGanadorGive(final Room room, final String ganadorId, final List<Card> cards) {

        final var culoId = this.playerRoleService.getPlayerIdByRole(room, PlayerRole.CULO).orElseThrow();
        final var roomWithoutCards = this.handManagementService.removeCardsFromHand(room, ganadorId, cards);
        return this.handManagementService.addCardsToHand(roomWithoutCards, culoId, cards);
    }

    /**
	 * Process subcampeon give.
	 *
	 * @param room         the room
	 * @param subcampeonId the subcampeon id
	 * @param cards        the cards
	 * @return the room
	 */
    private Room processSubcampeonGive(
            final Room room,
            final String subcampeonId,
            final List<Card> cards) {

        final var penultimoId = this.playerRoleService.getPlayerIdByRole(room, PlayerRole.PENULTIMO).orElseThrow();
        final var roomWithoutCards = this.handManagementService.removeCardsFromHand(room, subcampeonId, cards);
        return this.handManagementService.addCardsToHand(roomWithoutCards, penultimoId, cards);
    }

    /**
	 * Checks if is exchange complete.
	 *
	 * @param room the room
	 * @return true, if is exchange complete
	 */
    private boolean isExchangeComplete(final Room room) {

        final var ganadorDone = room.exchangeState().exchangeDone().contains(
                this.playerRoleService.getPlayerIdByRole(room, PlayerRole.GANADOR).orElse(""));
        final var subcampeonRole = this.playerRoleService.getPlayerIdByRole(room, PlayerRole.SUBCAMPEON);
        final var subcampeonDone = subcampeonRole.isEmpty()
                || room.exchangeState().exchangeDone().contains(subcampeonRole.get());
        return ganadorDone && subcampeonDone;
    }
}
