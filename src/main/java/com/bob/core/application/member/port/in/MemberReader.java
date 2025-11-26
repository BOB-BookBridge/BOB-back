package com.bob.core.application.member.port.in;

import java.util.UUID;

import com.bob.core.application.member.dto.result.MemberDetail;
import com.bob.core.domain.member.Member;

public interface MemberReader {

    Member read(UUID memberId);

    Member read(String email);

    MemberDetail readDetail(UUID memberId, boolean me);
}
