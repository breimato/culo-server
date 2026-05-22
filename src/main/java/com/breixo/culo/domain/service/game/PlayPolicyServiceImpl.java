package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.cards.Card;
import com.breixo.culo.domain.model.game.Play;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.GameSessionContext;
import com.breixo.culo.domain.port.input.game.PlayPolicyService;
import com.breixo.culo.domain.port.input.game.PlayRuleService;
import com.breixo.culo.domain.port.input.room.GameContextService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The Class PlayPolicyServiceImpl.
 */
@Service
@RequiredArgsConstructor
public class PlayPolicyServiceImpl implements PlayPolicyService {

    /** The game session context service. */
    private final GameContextService gameSessionContextService;

    /** The play rule service. */
    private final PlayRuleService playRuleService;

    /** {@inheritDoc} */
    @Override
    public void validatePlayerCanPlay(final GameSessionContext gameSessionContext) {

        this.gameSessionContextService.requirePlayerTurn(
                gameSessionContext.room(),
                gameSessionContext.player());
        this.gameSessionContextService.requirePlayerHasCards(
                gameSessionContext.room(),
                gameSessionContext.player());
    }

    /** {@inheritDoc} */
    @Override
    public void validateLegalPlay(final Play play, final Room room) {

        if (BooleanUtils.isFalse(this.playRuleService.isLegal(play, room.gameSession().currentRound()))) {
            throw new GameException(GameExceptionConstants.ILLEGAL_PLAY);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void validateCardsInHand(final List<Card> cards, final Room room, final String playerId) {

        final var hand = room.gameSession().hands().getOrDefault(playerId, List.of());

        if (BooleanUtils.isFalse(hand.containsAll(cards))) {
            throw new GameException(GameExceptionConstants.CARDS_NOT_IN_HAND);
        }
    }
}
