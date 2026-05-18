package com.breixo.culo.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class CuloException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;

    protected CuloException(final String codeAndMessage, final HttpStatus httpStatus) {
        super(codeAndMessage);
        this.code = codeAndMessage.split("\\|")[0].trim();
        this.httpStatus = httpStatus;
    }
}
