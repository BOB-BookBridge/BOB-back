package com.bob.support.fixture.post.domain;

import static com.bob.support.fixture.area.domain.AreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;

import java.util.UUID;

import com.bob.core.domain.post.Post;

public class PostFixture {

    public static Post createPost(UUID memberId, Integer categoryId, String bookStatus) {
        return Post.createPost(categoryId, EMD_AREA_ID, 1L, "제목", "설명", "https://image.jpg", bookStatus, memberId, 1L,
            30000, false);
    }

    public static Post createPost(String bookStatus) {
        return createPost(MEMBER_ID, 1, bookStatus);
    }

    public static Post createPost(Integer categoryId) {
        return createPost(MEMBER_ID, categoryId, "LOW");
    }

    public static Post createPost(UUID memberId) {
        return createPost(memberId, 1, "LOW");
    }

    public static Post createPost() {
        return createPost(MEMBER_ID);
    }
}
