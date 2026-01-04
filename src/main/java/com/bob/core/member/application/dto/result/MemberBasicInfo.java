package com.bob.core.member.application.dto.result;

import java.util.UUID;

public record MemberBasicInfo(UUID id, String nickname, String profileImageUrl) {

}
