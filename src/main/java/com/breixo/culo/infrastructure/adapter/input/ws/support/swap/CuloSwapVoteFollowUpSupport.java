package com.breixo.culo.infrastructure.adapter.input.ws.support.swap;

import com.breixo.culo.domain.model.swap.CuloSwapVoteResponse;
import com.breixo.culo.infrastructure.adapter.input.ws.room.RoomEventPublisher;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

/** The Class CuloSwapVoteFollowUpSupport. */
@Component
@RequiredArgsConstructor
public class CuloSwapVoteFollowUpSupport {

    /** The room event publisher. */
    private final RoomEventPublisher roomEventPublisher;

    /**
     * Publish vote follow up.
     *
     * @param culoVoteResult the culo vote result
     */
    public void publishVoteFollowUp(final CuloSwapVoteResponse culoVoteResult) {

        this.roomEventPublisher.publishRoomState(culoVoteResult.room());

        if (BooleanUtils.isFalse(culoVoteResult.votingFinished())) {
            return;
        }

        this.roomEventPublisher.publishCuloSwapResult(
                culoVoteResult.room(),
                culoVoteResult.swapAccepted());

        if (BooleanUtils.isTrue(culoVoteResult.swapAccepted())) {
            this.roomEventPublisher.publishAllHands(culoVoteResult.room());
        }
    }
}
