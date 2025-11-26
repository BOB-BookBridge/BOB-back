package com.bob.core.application.post;

import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bob.core.application.post.dto.query.ReadPostsQuery;
import com.bob.core.application.post.dto.result.PostSummaries;
import com.bob.core.application.post.dto.result.PostSummary;
import com.bob.core.application.post.port.in.PostReader;
import com.bob.core.domain.post.Post;
import com.bob.core.domain.post.repository.PostRepository;
import com.bob.core.domain.post.status.TradeProgress;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.post.dto.query.PostQueryFixture;

@ContainerTest
@DisplayName("게시글 목록 조회 필터링 테스트")
record PostQueryIntegrationTest(PostReader postReader, PostRepository postRepository, JdbcTemplate jdbcTemplate) {

    private static final Pageable PAGEABLE = PageRequest.of(0, 12);

    @Test
    void 제목_검색() {
        ReadPostsQuery query = PostQueryFixture.searchTitleQuery();

        PostSummaries result = postReader.readSummariesByQuery(query, PAGEABLE);

        assertThat(result.posts())
            .extracting(PostSummary::title)
            .allMatch(title -> title.contains("오브젝트"));
    }

    @Test
    void 저자_검색() {
        ReadPostsQuery query = PostQueryFixture.searchAuthorQuery();

        PostSummaries result = postReader.readSummariesByQuery(query, PAGEABLE);

        assertThat(result.posts())
            .extracting(PostSummary::title)
            .contains("자바 ORM 표준 JPA 프로그래밍");
    }

    @Test
    void 가격_필터_5000원_이하() {
        ReadPostsQuery query = PostQueryFixture.searchUnder5000PriceQuery();

        PostSummaries result = postReader.readSummariesByQuery(query, PAGEABLE);

        assertThat(result.posts())
            .extracting(PostSummary::price)
            .allMatch(price -> price <= 5000);
    }

    @Test
    void 가격_필터_5000원_이상_10000원_이하() {
        ReadPostsQuery query = PostQueryFixture.searchBetween_5000_10000_PriceQuery();

        PostSummaries result = postReader.readSummariesByQuery(query, PAGEABLE);

        assertThat(result.posts())
            .extracting(PostSummary::price)
            .allMatch(price -> price >= 5000 && price <= 10000);
    }

    @Test
    void 카테고리_필터() {
        postRepository.save(createPost(1));
        postRepository.save(createPost(13));

        ReadPostsQuery query = PostQueryFixture.searchCategoryQuery();

        PostSummaries result = postReader.readSummariesByQuery(query, PAGEABLE);

        assertThat(query.categoryIds()).contains(1, 12, 13, 14, 15, 16);
        assertThat(result.posts())
            .extracting(PostSummary::categoryId)
            .contains(1, 13);
    }

    @Test
    void 거래_상태_필터() {
        Post post = createPost();
        post.updateTradeProgress(TradeProgress.COMPLETED);
        postRepository.save(post);

        ReadPostsQuery query = PostQueryFixture.searchTradeStatusQuery();

        PostSummaries result = postReader.readSummariesByQuery(query, PAGEABLE);

        assertThat(result.posts())
            .extracting(PostSummary::status)
            .containsOnly("COMPLETED");
    }

    @Test
    void 책_상태_필터() {
        ReadPostsQuery query = PostQueryFixture.searchBookStatusQuery();

        PostSummaries result = postReader.readSummariesByQuery(query, PAGEABLE);

        assertThat(result.posts())
            .extracting(PostSummary::bookStatus)
            .contains("BEST");
    }

    @Test
    void 정렬_최신순() {
        ReadPostsQuery query = PostQueryFixture.searchNewestQuery();

        PostSummaries result = postReader.readSummariesByQuery(query, PAGEABLE);

        List<LocalDateTime> createdAtList = result.posts().stream()
            .map(PostSummary::createdAt)
            .toList();

        assertThat(createdAtList).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void 정렬_오래된순() {
        ReadPostsQuery query = PostQueryFixture.searchOldestQuery();

        PostSummaries result = postReader.readSummariesByQuery(query, PAGEABLE);

        List<LocalDateTime> createdAtList = result.posts().stream()
            .map(PostSummary::createdAt)
            .toList();

        assertThat(createdAtList).isSorted();
    }

    @Test
    void 정렬_낮은_가격순() {
        ReadPostsQuery query = PostQueryFixture.searchLowPriceQuery();

        PostSummaries result = postReader.readSummariesByQuery(query, PAGEABLE);

        List<Integer> prices = result.posts().stream()
            .map(PostSummary::price)
            .toList();

        assertThat(prices).isSorted();
    }

    @Test
    void 정렬_높은_가격순() {
        ReadPostsQuery query = PostQueryFixture.searchHighPriceQuery();

        PostSummaries result = postReader.readSummariesByQuery(query, PAGEABLE);

        List<Integer> prices = result.posts().stream()
            .map(PostSummary::price)
            .toList();

        assertThat(prices).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void 가격_필터_10000원_이상_20000원_이하() {
        ReadPostsQuery query = PostQueryFixture.searchBetween_10000_20000_PriceQuery();

        PostSummaries result = postReader.readSummariesByQuery(query, PAGEABLE);

        assertThat(result.posts())
            .extracting(PostSummary::price)
            .allMatch(price -> price >= 10000 && price <= 20000);
    }

    @Test
    void 가격_필터_20000원_이상() {
        ReadPostsQuery query = PostQueryFixture.searchOver20000PriceQuery();

        PostSummaries result = postReader.readSummariesByQuery(query, PAGEABLE);

        assertThat(result.posts())
            .extracting(PostSummary::price)
            .allMatch(price -> price >= 20000);
        assertThat(result.posts()).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void 지역_필터() {
        Integer emdId = 213; // 역삼동
        ReadPostsQuery query = PostQueryFixture.searchByEmdIdQuery(emdId);

        PostSummaries result = postReader.readSummariesByQuery(query, PAGEABLE);

        assertThat(result.posts()).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void 비활성화_게시글은_조회_목록_제외() {
        Post post = createPost();
        post.deactivate();

        postRepository.save(post);

        long allPostsCount = countAllPosts();
        long deactivatedPostsCount = countDeactivatedPosts();
        ReadPostsQuery query = PostQueryFixture.defaultReadFilteredPostsQuery();
        Pageable pageable = PageRequest.of(0, 12);

        PostSummaries result = postReader.readSummariesByQuery(query, pageable);

        assertThat(deactivatedPostsCount).isNotZero();
        assertThat(result.posts()).hasSize((int)(allPostsCount - deactivatedPostsCount));
    }

    private long countAllPosts() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM posts", Long.class);
    }

    private long countDeactivatedPosts() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM posts WHERE status = 'DEACTIVATED'", Long.class);
    }
}
