package com.bob.core.member.application.dto.result;

import java.util.List;

import com.bob.core.member.domain.Member;

public record MemberSummaries(Long totalCount, List<Member> members) {

}
