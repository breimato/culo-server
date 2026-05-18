package com.breixo.culo.domain.exception;

import org.springframework.http.HttpStatus;

public class GameException extends CuloException {

    public GameException(final String codeAndMessage) {
        super(codeAndMessage, HttpStatus.BAD_REQUEST);
    }
}
