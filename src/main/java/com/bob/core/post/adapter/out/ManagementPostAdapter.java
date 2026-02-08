package com.bob.core.post.adapter.out;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.bob.admin.post.application.port.out.ManagementPostPort;
import com.bob.admin.post.application.port.result.ManagementPost;
import com.bob.admin.post.application.port.result.ManagementPostSummaries;
import com.bob.admin.post.application.port.result.ManagementPostWriter;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.member.domain.Member;
import com.bob.core.post.application.dto.command.ChangePostStatusCommand;
import com.bob.core.post.application.dto.result.SearchPostsResult;
import com.bob.core.post.application.port.in.PostModifier;
import com.bob.core.post.application.port.in.PostReader;
import com.bob.core.post.application.port.in.PostSearcher;
import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.dsl.query.SearchManagementPostsQuery;

@Component
@RequiredArgsConstructor
public class ManagementPostAdapter implements ManagementPostPort {

    private final PostReader postReader;
    private final PostSearcher postSearcher;
    private final PostModifier postModifier;

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

    @Override
    public ManagementPost read(Long postId) {
        Post post = postReader.read(postId);

        return toManagementPost(post);
    }

    @Override
    public String changeStatus(Long postId, String status) {
        Post post = postModifier.changeStatus(postId, new ChangePostStatusCommand(status));

        return post.getStatus().name();
    }

    private ManagementPost toManagementPost(Post post) {
        Member writer = memberReader.read(post.getWriterId());

        return ManagementPost.builder()
            .id(post.getId())
            .title(post.getTitle())
            .thumbnailUrl(post.getThumbnailUrl())
            .status(post.getStatus().name())
            .createdAt(post.getCreatedAt())
            .writer(new ManagementPostWriter(
                writer.getId(),
                writer.getEmail(),
                writer.getNickname()
            ))
            .build();
    }

    private UUID getWriterId(String email) {
        return memberReader.read(email).getId();
    }
}
