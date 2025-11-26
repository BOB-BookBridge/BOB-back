package com.bob.integration.adapter.post;

import static com.bob.core.application.post.port.result.PostMemberWishResult.of;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.member.dto.result.MemberDetail;
import com.bob.core.application.member.port.in.MemberReader;
import com.bob.core.application.post.port.out.PostMemberPort;
import com.bob.core.application.post.port.result.PostMember;
import com.bob.core.application.post.port.result.PostMemberWishResult;

@Component
@RequiredArgsConstructor
public class PostMemberAdapter implements PostMemberPort {

    private final MemberReader readUseCase;

    @Override
    public PostMember read(UUID memberId) {
        MemberDetail detail = readUseCase.readDetail(memberId, false);

        List<PostMemberWishResult> wishes = detail.wishes().stream()
            .map((wish) -> of(wish.title(), wish.author(), wish.cover()))
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
