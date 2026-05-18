package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.CuloSwapInitiateCommand;
import com.breixo.culo.domain.model.Room;

public interface CuloSwapInitiateUseCase {

    Room execute(CuloSwapInitiateCommand culoSwapInitiateCommand);
}
