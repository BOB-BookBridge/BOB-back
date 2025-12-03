package com.bob.core.application.member;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.member.dto.result.MemberAreaDetail;
import com.bob.core.application.member.dto.result.MemberDetail;
import com.bob.core.application.member.dto.result.MemberWishDetail;
import com.bob.core.application.member.port.in.MemberReader;
import com.bob.core.application.member.port.out.MemberBookPort;
import com.bob.core.application.member.port.out.MemberBookcasePort;
import com.bob.core.application.member.port.result.MemberBookResult;
import com.bob.core.application.member.port.result.MemberBookcaseResult;
import com.bob.core.domain.member.Member;
import com.bob.core.domain.member.MemberInterest;
import com.bob.core.domain.member.MemberWish;
import com.bob.core.domain.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService implements MemberReader {

    private final MemberRepository memberRepository;

    private final MemberBookcasePort bookcasePort;
    private final MemberBookPort bookPort;

    public Member read(UUID memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. id : " + memberId));
    }

    public Member read(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. email : " + email));
    }

    @Transactional(readOnly = true)
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

        return MemberDetail.builder()
            .id(member.getId())
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
            .isSocial(member.getProvider() != null)
            .build();
    }
}
