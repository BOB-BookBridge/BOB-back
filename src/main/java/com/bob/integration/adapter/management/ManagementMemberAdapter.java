package com.bob.integration.adapter.management;

import static com.bob.core.application.management.port.result.ManagementMember.Area;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.bob.core.application.management.port.out.ManagementMemberPort;
import com.bob.core.application.management.port.result.ManagementMember;
import com.bob.core.application.management.port.result.ManagementMemberSummaries;
import com.bob.core.application.member.dto.result.MemberDetail;
import com.bob.core.application.member.dto.result.MemberSummaries;
import com.bob.core.application.member.port.in.MemberReader;
import com.bob.core.application.member.port.in.MemberSearcher;
import com.bob.core.domain.member.Member;
import com.bob.core.domain.member.repository.dsl.query.SearchKey;
import com.bob.core.domain.member.repository.dsl.query.SearchMembersQuery;

@Component
@RequiredArgsConstructor
public class ManagementMemberAdapter implements ManagementMemberPort {

    private final MemberReader memberReader;
    private final MemberSearcher memberSearcher;

    @Override
    public ManagementMember read(UUID memberId) {
        MemberDetail detail = memberReader.readDetail(memberId, false);

        return convertDetail(memberId, detail);
    }

    @Override
    public ManagementMemberSummaries search(String key, String keyword, Pageable pageable) {
        SearchMembersQuery query = new SearchMembersQuery(SearchKey.from(key), keyword);

        MemberSummaries summaries = memberSearcher.searchByQuery(query, pageable);

        List<ManagementMember> members = summaries.members().stream()
            .map(ManagementMemberAdapter::convert)
            .toList();

        return new ManagementMemberSummaries(summaries.totalCount(), members);
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

    private static ManagementMember convertDetail(UUID memberId, MemberDetail detail) {
        return ManagementMember.builder()
            .id(memberId)
            .status(detail.status())
            .role(detail.role())
            .email(detail.email())
            .nickname(detail.nickname())
            .area(new Area(detail.area().emdId(), detail.area().isAuthentication(), detail.area().authenticatedAt()))
            .memo(detail.memo())
            .lastActiveAt(detail.lastActiveAt())
            .createdAt(detail.createdAt())
            .build();
    }
}
