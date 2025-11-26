package com.bob.core.domain.post;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("게시글 찜 도메인 테스트")
class PostFavoriteTest {

    @Test
    void 게시글_찜_생성() {
        UUID memberId = MEMBER_ID;

        PostFavorite favorite = PostFavorite.createPostFavorite(memberId);

        assertThat(favorite).isNotNull();
        assertThat(favorite.getMemberId()).isEqualTo(memberId);
    }
}
