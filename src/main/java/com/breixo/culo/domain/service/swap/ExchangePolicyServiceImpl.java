package com.breixo.culo.domain.service.swap;

import com.breixo.culo.domain.constants.GameConstants;
import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import com.breixo.culo.domain.port.input.swap.ExchangePolicyService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The Class ExchangePolicyServiceImpl.
 */
@Service
public class ExchangePolicyServiceImpl implements ExchangePolicyService {

    /** {@inheritDoc} */
    @Override
    public void validateNotAlreadyDone(final Room room, final Player player) {

        if (room.exchangeState().exchangeDone().contains(player.id())) {
            throw new GameException(GameExceptionConstants.EXCHANGE_ALREADY_DONE);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void validateRoleCanExchange(final Player player) {

        final var role = player.role();
        final var isGanador = PlayerRole.GANADOR.getId().equals(role.getId());
        final var isSubcampeon = PlayerRole.SUBCAMPEON.getId().equals(role.getId());

        if (BooleanUtils.isFalse(isGanador) && BooleanUtils.isFalse(isSubcampeon)) {
            throw new GameException(GameExceptionConstants.NOT_IN_EXCHANGE);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void validateGiveCardsCount(final Player player, final List<Card> cards) {

        if (PlayerRole.GANADOR.getId().equals(player.role().getId())
                && BooleanUtils.isFalse(GameConstants.GANADOR_GIVE_COUNT.equals(cards.size()))) {
            throw new GameException(GameExceptionConstants.INVALID_EXCHANGE);
        }

        if (PlayerRole.SUBCAMPEON.getId().equals(player.role().getId())
                && BooleanUtils.isFalse(GameConstants.SUBCAMPEON_GIVE_COUNT.equals(cards.size()))) {
            throw new GameException(GameExceptionConstants.INVALID_EXCHANGE);
        }
    }
}
