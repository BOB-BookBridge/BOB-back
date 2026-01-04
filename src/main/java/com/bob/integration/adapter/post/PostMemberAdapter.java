package com.bob.integration.adapter.post;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.member.application.dto.result.MemberDetail;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.post.application.port.out.PostMemberPort;
import com.bob.core.post.application.port.result.PostMember;
import com.bob.core.post.application.port.result.PostMemberWishResult;

@Component
@RequiredArgsConstructor
public class PostMemberAdapter implements PostMemberPort {

    private final MemberReader memberReader;

    @Override
    public PostMember read(UUID memberId) {
        MemberDetail detail = memberReader.readDetail(memberId, false);

        List<PostMemberWishResult> wishes = detail.wishes().stream()
            .map((wish) -> new PostMemberWishResult(wish.title(), wish.author(), wish.cover()))
            .toList();

        return PostMember.builder()
            .id(detail.id())
            .nickname(detail.nickname())
            .emdId(detail.area().emdId())
            .authenticated(detail.area().isAuthentication())
            .profileImageUrl(detail.profileImageUrl())
            .interests(detail.interests())
            .wishes(wishes)
            .build();
    }
}
