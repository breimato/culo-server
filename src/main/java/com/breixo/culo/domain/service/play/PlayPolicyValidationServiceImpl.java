package com.breixo.culo.domain.service.play;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.card.Card;
import com.breixo.culo.domain.model.play.Play;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.session.GameSessionContext;
import com.breixo.culo.domain.port.input.play.PlayPolicyValidationService;
import com.breixo.culo.domain.port.input.play.PlayRuleService;
import com.breixo.culo.domain.port.input.session.GameSessionContextService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/** The Class PlayPolicyValidationServiceImpl. */
@Service
@RequiredArgsConstructor
public class PlayPolicyValidationServiceImpl implements PlayPolicyValidationService {

    /** The game session context service. */
    private final GameSessionContextService gameSessionContextService;

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
