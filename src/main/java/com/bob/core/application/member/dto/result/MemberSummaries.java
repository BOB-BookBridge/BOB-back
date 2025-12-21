package com.bob.core.application.member.dto.result;

import java.util.List;

import com.bob.core.domain.member.Member;

public record MemberSummaries(Long totalCount, List<Member> members) {

}
