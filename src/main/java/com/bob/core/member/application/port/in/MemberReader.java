package com.bob.core.member.application.port.in;

import java.util.UUID;

import com.bob.core.member.application.dto.result.MemberDetail;
import com.bob.core.member.domain.Member;

public interface MemberReader {

    Member read(UUID memberId);

    Member read(String email);

    MemberDetail readDetail(UUID memberId, boolean me);
}
