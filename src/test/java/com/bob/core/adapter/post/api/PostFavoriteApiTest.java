package com.bob.core.adapter.post.api;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.core.application.post.dto.result.PostSummaries;
import com.bob.core.domain.post.Post;
import com.bob.core.domain.post.repository.PostRepository;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;

@DisplayName("게시글 찜 API 테스트")
@BobApiTest
record PostFavoriteApiTest(MockMvcTester mvcTester, PostRepository postRepository, ObjectMapper objectMapper) {

    @Test
    void 게시글_찜_등록() {
        Post post = postRepository.save(createPost());
        setAuthentication();

        MvcTestResult result = mvcTester.post()
            .uri("/posts/{postId}/favorite", post.getId())
            .exchange();

        assertThat(result).hasStatus(201);

        Post updated = postRepository.findById(post.getId()).orElseThrow();
        assertThat(updated.isFavorite(MEMBER_ID)).isTrue();
    }

    @Test
    void 찜한_게시글_목록_조회() throws Exception {
        Post post1 = postRepository.save(createPost());
        Post post2 = postRepository.save(createPost());
        post1.addFavorite(MEMBER_ID);
        post2.addFavorite(MEMBER_ID);
        postRepository.save(post1);
        postRepository.save(post2);
        setAuthentication();

        MvcTestResult result = mvcTester.get()
            .uri("/posts/favorites")
            .exchange();

        assertThat(result).hasStatusOk();

        PostSummaries response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PostSummaries.class
        );

        assertThat(response.totalCount()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void 게시글_찜_해제() {
        Post post = postRepository.save(createPost());
        post.addFavorite(MEMBER_ID);
        postRepository.save(post);
        setAuthentication();

        MvcTestResult result = mvcTester.delete()
            .uri("/posts/{postId}/favorite", post.getId())
            .exchange();

        assertThat(result).hasStatusOk();

        Post updated = postRepository.findById(post.getId()).orElseThrow();
        assertThat(updated.isFavorite(MEMBER_ID)).isFalse();
    }

    void setAuthentication() {
        MemberDetails principal = new MemberDetails(MEMBER_ID, true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
