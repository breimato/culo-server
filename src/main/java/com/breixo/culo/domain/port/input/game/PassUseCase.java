package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.PassCommand;
import com.breixo.culo.domain.model.game.PassResult;

public interface PassUseCase {

    PassResult execute(PassCommand passCommand);
}
