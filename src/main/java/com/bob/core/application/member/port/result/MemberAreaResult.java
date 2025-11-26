package com.bob.core.application.member.port.result;

import lombok.Builder;

@Builder
public record MemberAreaResult(Integer emdId, String emdName, String siggName, String sidoName) {

    public static MemberAreaResult of(Integer emdId, String emdName, String siggName, String sidoName) {
        return MemberAreaResult.builder()
            .emdId(emdId)
            .emdName(emdName)
            .siggName(siggName)
            .sidoName(sidoName)
            .build();
    }
}
