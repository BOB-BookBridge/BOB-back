package com.bob.integration.adapter.management;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.bob.core.application.management.port.out.ManagementMemberPort;
import com.bob.core.application.management.port.result.ManagementMember;
import com.bob.core.application.management.port.result.ManagementMembersResult;
import com.bob.core.application.member.dto.result.MemberSummaries;
import com.bob.core.application.member.port.in.MemberSearcher;
import com.bob.core.domain.member.Member;
import com.bob.core.domain.member.repository.dsl.query.SearchKey;
import com.bob.core.domain.member.repository.dsl.query.SearchMembersQuery;

@Component
@RequiredArgsConstructor
public class ManagementMemberAdapter implements ManagementMemberPort {

    private final MemberSearcher memberSearcher;

    @Override
    public ManagementMembersResult search(String key, String keyword, Pageable pageable) {
        SearchMembersQuery query = new SearchMembersQuery(SearchKey.from(key), keyword);

        MemberSummaries summaries = memberSearcher.searchByQuery(query, pageable);

        List<ManagementMember> members = summaries.members().stream()
            .map(ManagementMemberAdapter::convert)
            .toList();

        return new ManagementMembersResult(summaries.totalCount(), members);
    }

    private static ManagementMember convert(Member member) {
        return ManagementMember.builder()
            .id(member.getId())
            .status(member.getStatus().name())
            .role(member.getRole().name())
            .email(member.getEmail())
            .nickname(member.getNickname())
            .lastActiveAt(member.getLastActiveAt())
            .createdAt(member.getCreatedAt())
            .build();
    }
}
