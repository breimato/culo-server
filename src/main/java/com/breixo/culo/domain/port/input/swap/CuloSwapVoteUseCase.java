package com.breixo.culo.domain.port.input.swap;

import com.breixo.culo.domain.command.swap.CuloSwapVoteCommand;
import com.breixo.culo.domain.model.swap.CuloSwapVoteResponse;

/** The Interface CuloSwapVoteUseCase. */
public interface CuloSwapVoteUseCase {

    /**
     * Execute.
     *
     * @param culoSwapVoteCommand the culo swap vote command
     * @return the culo swap vote result
     */
    CuloSwapVoteResponse execute(CuloSwapVoteCommand culoSwapVoteCommand);
}
