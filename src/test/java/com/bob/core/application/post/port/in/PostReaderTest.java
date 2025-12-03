package com.bob.core.application.post.port.in;

import static com.bob.global.exception.response.ApplicationError.POST_ACCESS_DENIED;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static com.bob.support.fixture.post.dto.query.PostQueryFixture.defaultReadFilteredPostsQuery;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.bob.core.application.post.dto.query.ReadMemberPostsQuery;
import com.bob.core.application.post.dto.query.ReadPostDetailQuery;
import com.bob.core.application.post.dto.query.ReadPostFavoritesQuery;
import com.bob.core.application.post.dto.result.PostDetail;
import com.bob.core.application.post.dto.result.PostSummaries;
import com.bob.core.domain.file.repository.FileRepository;
import com.bob.core.domain.post.Post;
import com.bob.core.domain.post.repository.PostRepository;
import com.bob.core.domain.post.status.Status;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.file.domain.FileFixture;

@DisplayName("게시글 조회 테스트")
@ContainerTest
record PostReaderTest(PostReader postReader, PostRepository postRepository, FileRepository fileRepository) {

    @Test
    void 게시글_조회() {
        Post post = postRepository.save(createPost());

        Post result = postReader.read(post.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(post.getId());
    }

    @Test
    void 게시글_조회_시_존재하지_않으면_사용자_예외가_발생한다() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> postReader.read(nonExistentId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("게시글을 찾을 수 없습니다.");
    }

    @Test
    void 회원_기반_게시글_목록_조회() {
        UUID memberId = UUID.randomUUID();
        Post post1 = postRepository.save(createPost(memberId));
        Post post2 = postRepository.save(createPost(memberId));

        ReadMemberPostsQuery query = new ReadMemberPostsQuery(memberId);

        List<Post> results = postReader.readByMember(query);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Post::getId).containsExactly(post1.getId(), post2.getId());
    }

    @Test
    void 회원_기반_게시글_목록_조회_시_게시글이_없으면_빈_리스트_반환() {
        UUID memberId = UUID.randomUUID();

        ReadMemberPostsQuery query = new ReadMemberPostsQuery(memberId);

        List<Post> results = postReader.readByMember(query);

        assertThat(results).isEmpty();
    }

    @Test
    void 조건_기반_게시글_요약_목록_조회() {
        postRepository.save(createPost());
        postRepository.save(createPost());
        Pageable pageable = PageRequest.of(0, 10);

        PostSummaries results = postReader.readSummariesByQuery(defaultReadFilteredPostsQuery(), pageable);

        assertThat(results.posts()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void 찜한_게시글_요약_목록_조회() {
        Post post = postRepository.save(createPost());
        post.addFavorite(MEMBER_ID);
        postRepository.save(post);

        ReadPostFavoritesQuery query = new ReadPostFavoritesQuery(MEMBER_ID);
        Pageable pageable = PageRequest.of(0, 10);

        PostSummaries results = postReader.readFavoriteSummariesByQuery(query, pageable);

        assertThat(results.totalCount()).isGreaterThanOrEqualTo(1);
        assertThat(results.posts()).isNotEmpty();
    }

    @Test
    void 찜한_게시글_요약_목록_조회_시_찜이_없으면_빈_리스트_반환() {
        UUID memberId = UUID.randomUUID();
        ReadPostFavoritesQuery query = new ReadPostFavoritesQuery(memberId);
        Pageable pageable = PageRequest.of(0, 10);

        PostSummaries results = postReader.readFavoriteSummariesByQuery(query, pageable);

        assertThat(results.totalCount()).isZero();
        assertThat(results.posts()).isEmpty();
    }

    @Test
    void 게시글_상세_조회() {
        Post post = postRepository.save(createPost());
        fileRepository.save(FileFixture.createFile("post/testfile.png", 0, String.valueOf(post.getId())));

        ReadPostDetailQuery query = new ReadPostDetailQuery(MEMBER_ID, true);

        PostDetail result = postReader.readDetail(post.getId(), query);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(post.getId());
        assertThat(result.title()).isEqualTo(post.getTitle());
        assertThat(result.images().get(0).fileName()).isEqualTo("post/testfile.png");
    }

    @Test
    void 게시글_상세_조회_시_작성자인_경우_isOwner_true() {
        Post post = postRepository.save(createPost());
        ReadPostDetailQuery query = new ReadPostDetailQuery(MEMBER_ID, true);

        PostDetail result = postReader.readDetail(post.getId(), query);

        assertThat(result.isOwner()).isTrue();
    }

    @Test
    void 게시글_상세_조회_시_존재하지_않으면_사용자_예외가_발생한다() {
        Long nonExistentId = 999L;
        ReadPostDetailQuery query = new ReadPostDetailQuery(MEMBER_ID, true);

        assertThatThrownBy(() -> postReader.readDetail(nonExistentId, query))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("게시글을 찾을 수 없습니다.");
    }

    @Test
    void 클라이언트가_비활성화된_게시글_상세_조회_시_사용자_예외가_발생한다() {
        Post post = postRepository.save(createPost());
        post.deactivate();
        postRepository.save(post);

        ReadPostDetailQuery query = new ReadPostDetailQuery(MEMBER_ID, true);

        assertThatThrownBy(() -> postReader.readDetail(post.getId(), query))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(POST_ACCESS_DENIED.getMessage());
    }

    @Test
    void 클라이언트가_보류된_게시글_상세_조회_시_사용자_예외가_발생한다() {
        Post post = postRepository.save(createPost());
        ReflectionTestUtils.setField(post, "status", Status.WITHHELD);
        postRepository.save(post);

        ReadPostDetailQuery query = new ReadPostDetailQuery(MEMBER_ID, true);

        assertThatThrownBy(() -> postReader.readDetail(post.getId(), query))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(POST_ACCESS_DENIED.getMessage());
    }

    @Test
    void 서버_비활성화_게시글_조회() {
        Post post = postRepository.save(createPost());
        post.deactivate();
        postRepository.save(post);

        ReadPostDetailQuery query = new ReadPostDetailQuery(MEMBER_ID, false);

        PostDetail result = postReader.readDetail(post.getId(), query);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(post.getId());
    }
}
