package com.breixo.culo.domain.service.game;

import com.breixo.culo.domain.model.game.RoundClosure;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.input.game.PlayRuleService;
import com.breixo.culo.domain.port.input.room.GameContextService;
import com.breixo.culo.domain.port.input.game.RoundClosingService;
import com.breixo.culo.domain.port.input.game.TurnManagementService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

/** The Class RoundClosingServiceImpl. */
@Service
@RequiredArgsConstructor
public class RoundClosingServiceImpl implements RoundClosingService {

    /** The play rule service. */
    private final PlayRuleService playRuleService;

    /** The turn management service. */
    private final TurnManagementService turnManagementService;

    /** The game session context service. */
    private final GameContextService gameSessionContextService;

    /** {@inheritDoc} */
    @Override
    public RoundClosure closeRoundIfOthersAllPassed(final Room room) {

        final var activePlayerIds = this.gameSessionContextService.activePlayerIds(room);

        if (BooleanUtils.isFalse(this.playRuleService.isRoundOver(room.gameSession().currentRound(), activePlayerIds))) {
            return RoundClosure.builder()
                    .room(room)
                    .roundClosed(false)
                    .build();
        }

        final var roomAfterClose = this.turnManagementService.finishRoundAndSetOpener(room);

        return RoundClosure.builder()
                .room(roomAfterClose)
                .roundClosed(true)
                .build();
    }
}
