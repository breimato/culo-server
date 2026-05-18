package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.CuloSwapVoteCommand;
import com.breixo.culo.domain.model.game.CuloSwapVoteResult;

public interface CuloSwapVoteUseCase {

    CuloSwapVoteResult execute(CuloSwapVoteCommand culoSwapVoteCommand);
}
