package com.bob.core.member.application.port.in;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.bob.core.member.application.dto.result.MemberBasicInfo;
import com.bob.core.member.application.dto.result.MemberDetail;
import com.bob.core.member.domain.Member;

public interface MemberReader {

    Member read(UUID memberId);

    Member read(String email);

    Optional<Member> findByEmail(String email);

    MemberBasicInfo readBasicInfo(UUID memberId);

    MemberDetail readDetail(UUID memberId, boolean me);

    List<UUID> readAllActiveMemberIds();
}
