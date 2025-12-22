package com.bob.core.post.application.dto.result;

import java.util.List;
import java.util.UUID;

import com.bob.core.post.domain.Post;

public record PostSummaries(Long totalCount, List<PostSummary> posts) {

    public static PostSummaries of(Long totalCount, List<Post> postList, UUID memberId) {
        List<PostSummary> summaries = postList.stream()
            .map(post -> PostSummary.of(post, memberId))
            .toList();

        return new PostSummaries(totalCount, summaries);
    }
}
