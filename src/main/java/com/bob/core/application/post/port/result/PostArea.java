package com.bob.core.application.post.port.result;

import lombok.Builder;

@Builder
public record PostArea(int emdId, String emdName, String siggName) {

    public static PostArea of(int emdId, String emdName, String siggName) {
        return PostArea.builder()
            .emdId(emdId)
            .emdName(emdName)
            .siggName(siggName)
            .build();
    }
}
