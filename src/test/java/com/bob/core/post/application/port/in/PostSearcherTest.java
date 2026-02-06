package com.bob.core.post.application.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.post.domain.PostFixture.createPendingPost;
import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.bob.core.post.application.dto.result.SearchPostsResult;
import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.PostRepository;
import com.bob.core.post.domain.repository.dsl.query.SearchManagementPostsQuery;
import com.bob.core.post.domain.status.Status;
import com.bob.support.annotation.ContainerTest;

@DisplayName("게시글 검색 테스트")
@ContainerTest
record PostSearcherTest(PostSearcher postSearcher, PostRepository postRepository) {

    @Test
    void 게시글_검색() {
        postRepository.save(createPendingPost());

        var query = new SearchManagementPostsQuery(null, null);
        var pageable = PageRequest.of(0, 20);

        SearchPostsResult result = postSearcher.searchByQuery(query, pageable);

        assertThat(result.totalCount()).isGreaterThanOrEqualTo(1);
        assertThat(result.posts()).isNotEmpty();
        assertThat(result.posts())
            .allSatisfy(post -> assertThat(post.getStatus()).isIn(Status.PENDING, Status.BANNED));
    }

    @Test
    void 게시글_검색_상태_필터링() {
        Post post = createPost();
        ReflectionTestUtils.setField(post, "status", Status.BANNED);
        postRepository.save(post);

        var query = new SearchManagementPostsQuery(null, "BANNED");
        var pageable = PageRequest.of(0, 20);

        SearchPostsResult result = postSearcher.searchByQuery(query, pageable);

        assertThat(result.posts()).isNotEmpty();
        assertThat(result.posts())
            .allSatisfy(post1 -> assertThat(post1.getStatus()).isEqualTo(Status.BANNED));
    }

    @Test
    void 게시글_검색_작성자_필터링() {
        postRepository.save(createPendingPost(MEMBER_ID));

        var query = new SearchManagementPostsQuery(MEMBER_ID, null);
        var pageable = PageRequest.of(0, 20);

        SearchPostsResult result = postSearcher.searchByQuery(query, pageable);

        assertThat(result.posts()).isNotEmpty();
        assertThat(result.posts())
            .allSatisfy(post -> assertThat(post.getWriterId()).isEqualTo(MEMBER_ID));
    }
}
