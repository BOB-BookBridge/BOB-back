package com.bob.core.member.adapter.api.response;

import java.time.LocalDate;

import com.bob.core.member.application.dto.result.MemberAreaDetail;

public record MemberAreaResponse(int emdId, boolean isAuthentication, LocalDate authenticatedAt) {

    public static MemberAreaResponse of(MemberAreaDetail area) {
        return new MemberAreaResponse(area.emdId(), area.isAuthentication(), area.authenticatedAt());
    }
}
