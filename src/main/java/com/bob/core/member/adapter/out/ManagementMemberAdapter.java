package com.bob.core.member.adapter.out;

import static com.bob.admin.member.application.port.result.ManagementMember.Area;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.bob.admin.member.application.port.out.ManagementMemberPort;
import com.bob.admin.member.application.port.result.ManagementMember;
import com.bob.admin.member.application.port.result.ManagementMemberSummaries;
import com.bob.core.member.application.dto.command.ChangeStatusCommand;
import com.bob.core.member.application.dto.result.MemberDetail;
import com.bob.core.member.application.dto.result.MemberSummaries;
import com.bob.core.member.application.port.in.MemberModifier;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.member.application.port.in.MemberSearcher;
import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.repository.dsl.query.SearchKey;
import com.bob.core.member.domain.repository.dsl.query.SearchMembersQuery;

@Component
@RequiredArgsConstructor
public class ManagementMemberAdapter implements ManagementMemberPort {

    private final MemberReader memberReader;
    private final MemberSearcher memberSearcher;
    private final MemberModifier memberModifier;

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

    @Override
    public ManagementMember changeStatus(UUID memberId, String status, String memo) {
        ChangeStatusCommand command = new ChangeStatusCommand(status, memo);

        Member member = memberModifier.changeStatus(memberId, command);

        return convert(member);
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
