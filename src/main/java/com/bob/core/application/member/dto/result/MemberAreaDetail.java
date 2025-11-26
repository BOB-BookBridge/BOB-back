package com.bob.core.application.member.dto.result;

import java.time.LocalDate;

public record MemberAreaDetail(int emdId, boolean isAuthentication, LocalDate authenticatedAt) {

}
