package com.bob.core.adapter.member.api.response;

import java.time.LocalDate;

import com.bob.core.application.member.dto.result.MemberAreaDetail;

public record MemberAreaResponse(int emdId, boolean isAuthentication, LocalDate authenticatedAt) {

    public static MemberAreaResponse of(MemberAreaDetail area) {
        return new MemberAreaResponse(area.emdId(), area.isAuthentication(), area.authenticatedAt());
    }
}
