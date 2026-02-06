package com.bob.core.post.adapter.out;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.bob.admin.post.application.port.out.ManagementPostPort;
import com.bob.admin.post.application.port.result.ManagementPost;
import com.bob.admin.post.application.port.result.ManagementPostSummaries;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.post.application.dto.result.SearchPostsResult;
import com.bob.core.post.application.port.in.PostSearcher;
import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.dsl.query.SearchManagementPostsQuery;

@Component
@RequiredArgsConstructor
public class ManagementPostAdapter implements ManagementPostPort {

    private final PostSearcher postSearcher;
    private final MemberReader memberReader;

    @Override
    public ManagementPostSummaries readAll(String email, String status, Pageable pageable) {
        UUID writerId = email != null ? getWriterId(email) : null;
        SearchManagementPostsQuery query = new SearchManagementPostsQuery(writerId, status);

        SearchPostsResult result = postSearcher.searchByQuery(query, pageable);

        List<ManagementPost> managementPosts = result.posts().stream()
            .map(this::toManagementPost)
            .toList();

        return new ManagementPostSummaries(result.totalCount(), managementPosts);
    }

    private ManagementPost toManagementPost(Post post) {
        String writerEmail = getEmail(post.getWriterId());

        return ManagementPost.builder()
            .id(post.getId())
            .title(post.getTitle())
            .writerEmail(writerEmail)
            .status(post.getStatus().name())
            .createdAt(post.getCreatedAt())
            .build();
    }

    private UUID getWriterId(String email) {
        return memberReader.read(email).getId();
    }

    private String getEmail(UUID memberId) {
        return memberReader.read(memberId).getEmail();
    }
}
