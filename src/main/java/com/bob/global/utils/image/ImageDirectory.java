package com.bob.global.utils.image;

import lombok.Getter;

@Getter
public enum ImageDirectory {
    PROFILE("profile/"), POST("post/"), CHAT("chat/");

    private final String prefix;

    ImageDirectory(String prefix) {
        this.prefix = prefix;
    }

    public static ImageDirectory of(String name) {
        return ImageDirectory.valueOf(name.toUpperCase());
    }
}
