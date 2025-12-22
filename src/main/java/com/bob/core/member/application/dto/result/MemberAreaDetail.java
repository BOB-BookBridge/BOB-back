package com.bob.core.member.application.dto.result;

import java.time.LocalDate;

public record MemberAreaDetail(int emdId, boolean isAuthentication, LocalDate authenticatedAt) {

}
