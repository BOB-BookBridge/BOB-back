package com.bob.support.fixture.post.domain;

import static com.bob.support.fixture.area.domain.AreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;

import java.util.List;
import java.util.UUID;

import com.bob.core.post.domain.Post;

public class PostFixture {

    public static Post createPost(UUID memberId, Integer categoryId, String bookStatus) {
        return Post.createPost(categoryId, EMD_AREA_ID, 1L, "title", "description", "https://image.jpg", bookStatus,
            memberId, 1L, 30000, false, List.of());
    }

    public static Post createPost(UUID memberId, Integer categoryId, Integer emdId, String bookStatus) {
        return Post.createPost(categoryId, emdId, 1L, "title", "description", "https://image.jpg", bookStatus,
            memberId, 1L, 30000, false, List.of());
    }

    public static Post createPost(UUID memberId, Integer categoryId, String bookStatus, List<String> filterWords) {
        return Post.createPost(categoryId, EMD_AREA_ID, 1L, "title", "description", "https://image.jpg", bookStatus,
            memberId, 1L, 30000, false, filterWords);
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

    public static Post createPendingPost() {
        return createPost(MEMBER_ID, 1, "LOW", List.of("forbidden"));
    }

    public static Post createPendingPost(UUID memberId) {
        return createPost(memberId, 1, "LOW", List.of("forbidden"));
    }
}
