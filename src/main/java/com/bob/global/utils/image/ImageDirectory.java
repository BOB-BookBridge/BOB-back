package com.bob.global.utils.image;

import static com.bob.global.exception.response.ApplicationError.UN_SUPPORTED_DOMAIN;

import lombok.Getter;

import com.bob.global.exception.exceptions.ApplicationException;

@Getter
public enum ImageDirectory {
    PROFILE("profile/"),
    POST("post/"),
    CHAT("chat/");

    private final String prefix;

    ImageDirectory(String prefix) {
        this.prefix = prefix;
    }

    public static ImageDirectory from(String name) {
        try {
            return ImageDirectory.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(UN_SUPPORTED_DOMAIN);
        }
    }
}
