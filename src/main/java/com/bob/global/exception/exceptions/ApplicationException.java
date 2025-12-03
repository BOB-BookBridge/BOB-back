package com.bob.global.exception.exceptions;

import lombok.Getter;

import com.bob.global.exception.response.ApplicationError;

@Getter
public class ApplicationException extends RuntimeException {

    private final ApplicationError error;
    private final Object[] args;

    public ApplicationException(ApplicationError error, Object... args) {
        super((args == null || args.length == 0)
            ? error.getMessage()
            : String.format(error.getMessage(), args));

        this.error = error;
        this.args = args;
    }
}
