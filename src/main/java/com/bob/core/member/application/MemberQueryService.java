package com.bob.core.member.application;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.member.application.dto.result.MemberAreaDetail;
import com.bob.core.member.application.dto.result.MemberBasicInfo;
import com.bob.core.member.application.dto.result.MemberDetail;
import com.bob.core.member.application.dto.result.MemberSummaries;
import com.bob.core.member.application.dto.result.MemberWishDetail;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.member.application.port.in.MemberSearcher;
import com.bob.core.member.application.port.out.MemberBookPort;
import com.bob.core.member.application.port.out.MemberBookcasePort;
import com.bob.core.member.application.port.result.MemberBookResult;
import com.bob.core.member.application.port.result.MemberBookcaseResult;
import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.MemberInterest;
import com.bob.core.member.domain.MemberWish;
import com.bob.core.member.domain.repository.MemberRepository;
import com.bob.core.member.domain.repository.dsl.query.SearchMembersQuery;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService implements MemberReader, MemberSearcher {

    private final MemberRepository memberRepository;

    private final MemberBookcasePort bookcasePort;
    private final MemberBookPort bookPort;

    @Override
    public Member read(UUID memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. id : " + memberId));
    }

    @Override
    public Member read(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. email : " + email));
    }

    @Override
    public MemberBasicInfo readBasicInfo(UUID memberId) {
        Member member = read(memberId);

        return new MemberBasicInfo(member.getId(), member.getNickname(), member.getProfileImageUrl());
    }

    @Transactional
    public MemberDetail readDetail(UUID memberId, boolean me) {
        Member member = read(memberId);

        if (member.isDeactivated()) {
            return MemberDetail.deactivateMemberDetail(
                member.getId(),
                new MemberAreaDetail(
                    member.getArea().getEmdId(),
                    member.getArea().isAuthenticated(),
                    member.getArea().getAuthenticatedAt()
                ),
                member.getProvider()
            );
        }

        // 관심사
        List<String> interests = member.getInterests().stream().map(MemberInterest::getDisplayName).toList();

        // 책장
        List<MemberBookcaseResult> bookcase = bookcasePort.readAllDetail(memberId);

        // 희망 도서
        List<Long> bookIds = member.getWishes().stream().map(MemberWish::getBookId).toList();
        List<MemberBookResult> books = bookPort.readAll(bookIds);
        List<MemberWishDetail> wishes = MemberWishDetail.listFrom(member.getWishes(), books);

        if (me)
            member.updateLastActiveTime();

        return MemberDetail.builder()
            .id(member.getId())
            .status(member.getStatus().name())
            .role(member.getRole().name())
            .email(member.getEmail())
            .nickname(member.getNickname())
            .profileImageUrl(member.getProfileImageUrl())
            .area(new MemberAreaDetail(
                member.getArea().getEmdId(),
                member.getArea().isAuthenticated(),
                member.getArea().getAuthenticatedAt()
            ))
            .interests(interests)
            .bookcase(bookcase)
            .wishes(wishes)
            .memo(member.getMemo())
            .isSocial(member.getProvider() != null)
            .lastActiveAt(member.getLastActiveAt())
            .createdAt(member.getCreatedAt())
            .build();
    }

    public MemberSummaries searchByQuery(SearchMembersQuery query, Pageable pageable) {
        List<Member> members = memberRepository.findMembers(query, pageable);
        Long totalCount = memberRepository.countMembers(query);

        return new MemberSummaries(totalCount, members);
    }
}
