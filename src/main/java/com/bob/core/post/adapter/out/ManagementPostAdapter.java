package com.bob.core.post.adapter.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bob.admin.post.application.port.out.ManagementPostPort;
import com.bob.admin.post.application.port.result.ManagementPost;
import com.bob.admin.post.application.port.result.ManagementPostSummaries;
import com.bob.admin.post.application.port.result.ManagementPostWriter;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.member.domain.Member;
import com.bob.core.post.application.dto.command.ChangePostStatusCommand;
import com.bob.core.post.application.dto.query.ReadPostDetailQuery;
import com.bob.core.post.application.dto.result.PostDetail;
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
        UUID writerId = StringUtils.hasText(email) ? getWriterId(email) : null;
        SearchManagementPostsQuery query = new SearchManagementPostsQuery(writerId, status);

        SearchPostsResult result = postSearcher.searchByQuery(query, pageable);

        List<ManagementPost> managementPosts = result.posts().stream()
            .map(this::toManagementPost)
            .toList();

        return new ManagementPostSummaries(result.totalCount(), managementPosts);
    }

    @Override
    public ManagementPost read(Long postId) {
        ReadPostDetailQuery query = new ReadPostDetailQuery(null, false);

        PostDetail detail = postReader.readDetail(postId, query);

        return toManagementPost(detail);
    }

    @Override
    public String changeStatus(Long postId, String status) {
        Post post = postModifier.changeStatus(postId, new ChangePostStatusCommand(status));

        return post.getStatus().name();
    }

    private ManagementPost toManagementPost(PostDetail detail) {
        return toManagementPost(detail.id(), detail.title(), detail.thumbnailUrl(),
            detail.description(), detail.status(), detail.createdAt(),
            detail.writer().id(), detail.filterWords());
    }

    private ManagementPost toManagementPost(Post post) {
        return toManagementPost(post.getId(), post.getTitle(), post.getThumbnailUrl(),
            post.getDescription(), post.getStatus().name(), post.getCreatedAt(),
            post.getWriterId(), null);
    }

    private ManagementPost toManagementPost(Long id, String title, String thumbnailUrl,
        String description, String status, LocalDateTime createdAt,
        UUID writerId, List<String> filterWords
    ) {
        Member writer = memberReader.read(writerId);

        return ManagementPost.builder()
            .id(id)
            .title(title)
            .thumbnailUrl(thumbnailUrl)
            .description(description)
            .status(status)
            .createdAt(createdAt)
            .writer(new ManagementPostWriter(
                writer.getId(),
                writer.getEmail(),
                writer.getNickname()
            ))
            .filterWords(filterWords)
            .build();
    }

    private UUID getWriterId(String email) {
        return memberReader.read(email).getId();
    }
}
