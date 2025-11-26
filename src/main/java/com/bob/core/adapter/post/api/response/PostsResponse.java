package com.bob.core.adapter.post.api.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.bob.core.application.post.dto.result.PostSummaries;
import com.bob.core.application.post.dto.result.PostSummary;

public record PostsResponse(Long totalCount, List<PostResponse> posts) {

    public static PostsResponse from(PostSummaries result, Map<Long, String> statusMap) {
        List<PostResponse> list = result.posts().stream()
            .map(p -> {
                String resolved = "OWNER".equals(p.participation()) ? "OWNER" : statusMap.getOrDefault(p.id(), "NONE");
                return PostResponse.from(p, resolved);
            }).toList();

        return new PostsResponse(result.totalCount(), list);
    }

    public record PostResponse(
        Long id,
        Integer categoryId,
        String title,
        String status,
        String thumbnailUrl,
        String bookStatus,
        Integer price,
        LocalDateTime createdAt,
        String participation
    ) {

        public static PostResponse from(PostSummary summary, String participation) {
            return new PostResponse(
                summary.id(),
                summary.categoryId(),
                summary.title(),
                summary.status(),
                summary.thumbnailUrl(),
                summary.bookStatus(),
                summary.price(),
                summary.createdAt(),
                participation
            );
        }
    }
}
