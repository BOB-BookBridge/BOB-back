package com.bob.core.adapter.post.api;

import static com.bob.core.domain.trade.status.Status.REQUESTED;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static com.bob.support.fixture.post.dto.request.CreatePostRequestFixture.createCreatePostRequest;
import static com.bob.support.fixture.trade.domain.TradeFixture.createTrade;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.core.adapter.post.api.request.ChangePostInfoRequest;
import com.bob.core.adapter.post.api.response.CreatePostResponse;
import com.bob.core.adapter.post.api.response.PostDetailResponse;
import com.bob.core.domain.post.Post;
import com.bob.core.domain.post.repository.PostRepository;
import com.bob.core.domain.trade.Trade;
import com.bob.core.domain.trade.repository.TradeRepository;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;

@DisplayName("게시글 API 테스트")
@BobApiTest
record PostApiTest(
    MockMvcTester mvcTester, PostRepository postRepository, TradeRepository tradeRepository, ObjectMapper objectMapper
) {

    @Test
    void 게시글_생성() throws Exception {
        setAuthentication(MEMBER_ID);

        MvcTestResult result = mvcTester.post()
            .uri("/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createCreatePostRequest())
            .exchange();

        assertThat(result).hasStatus(201);

        CreatePostResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            CreatePostResponse.class
        );

        assertThat(response.id()).isNotNull();
    }

    @Test
    void 게시글_목록_조회() {
        postRepository.save(createPost());
        postRepository.save(createPost());

        MvcTestResult result = mvcTester.get()
            .uri("/posts")
            .exchange();

        assertThat(result).hasStatusOk();
    }

    @Test
    void 게시글_목록_조회_인증_설정() {
        postRepository.save(createPost());
        postRepository.save(createPost());
        setAuthentication(MEMBER_ID);

        MvcTestResult result = mvcTester.get()
            .uri("/posts")
            .exchange();

        assertThat(result).hasStatusOk();
    }

    @Test
    void 게시글_상세_조회() throws Exception {
        Post post = postRepository.save(createPost());
        setAuthentication(MEMBER_ID);

        MvcTestResult result = mvcTester.get()
            .uri("/posts/{postId}", post.getId())
            .exchange();

        assertThat(result).hasStatusOk();

        PostDetailResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PostDetailResponse.class
        );

        assertThat(response.id()).isEqualTo(post.getId());

        assertThat(response.trade()).isNull();
    }

    @Test
    void 게시글_상세_조회_시_거래_요청_이력이_있다면_거래_정보_포함() throws Exception {
        Post post = postRepository.save(createPost());
        Trade postTrade = tradeRepository.save(createTrade(post.getId(), REQUESTED));

        setAuthentication(OTHER_MEMBER_ID);

        MvcTestResult result = mvcTester.get()
            .uri("/posts/{postId}", post.getId())
            .exchange();

        assertThat(result).hasStatusOk();

        PostDetailResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PostDetailResponse.class
        );

        assertThat(response.id()).isEqualTo(post.getId());

        assertThat(response.trade()).isNotNull();
        assertThat(response.trade().id()).isEqualTo(postTrade.getId());
        assertThat(response.trade().status()).isEqualTo("REQUESTED");
    }

    @Test
    void 게시글_정보_수정() throws Exception {
        Post post = postRepository.save(createPost());
        setAuthentication(MEMBER_ID);

        ChangePostInfoRequest request = new ChangePostInfoRequest("BEST", "새로운 설명", true);

        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.patch()
            .uri("/posts/{postId}", post.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatusOk();

        Post updated = postRepository.findById(post.getId()).orElseThrow();
        assertThat(updated.getBookStatus().name()).isEqualTo("BEST");
        assertThat(updated.getDescription()).isEqualTo("새로운 설명");
        assertThat(updated.isWishOnly()).isTrue();
    }

    @Test
    void 게시글_삭제() {
        Post post = postRepository.save(createPost());
        setAuthentication(MEMBER_ID);

        MvcTestResult result = mvcTester.delete()
            .uri("/posts/{postId}", post.getId())
            .exchange();

        assertThat(result).hasStatusOk();

        Post deleted = postRepository.findById(post.getId()).orElseThrow();
        assertThat(deleted.isActive()).isFalse();
    }

    void setAuthentication(UUID memberId) {
        MemberDetails principal = new MemberDetails(memberId, true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
