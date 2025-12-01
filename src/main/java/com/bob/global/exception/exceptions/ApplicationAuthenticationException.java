package com.bob.global.exception.exceptions;

import lombok.Getter;

import org.springframework.security.core.AuthenticationException;

import com.bob.global.exception.response.AuthenticationError;

@Getter
public class ApplicationAuthenticationException extends AuthenticationException {

    private final AuthenticationError error;
    private final String customMessage;

    public ApplicationAuthenticationException(AuthenticationError error) {
        super(error.getMessage());
        this.error = error;
        this.customMessage = null;
    }

    public ApplicationAuthenticationException(AuthenticationError error, String customMessage) {
        super(customMessage);
        this.error = error;
        this.customMessage = customMessage;
    }
}
