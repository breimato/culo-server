package com.breixo.culo.domain.exception;

import org.springframework.http.HttpStatus;

public class RoomException extends CuloException {

    public RoomException(final String codeAndMessage) {
        super(codeAndMessage, HttpStatus.BAD_REQUEST);
    }

    public RoomException(final String codeAndMessage, final HttpStatus httpStatus) {
        super(codeAndMessage, httpStatus);
    }
}
