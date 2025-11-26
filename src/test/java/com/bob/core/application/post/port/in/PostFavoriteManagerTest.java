package com.bob.core.application.post.port.in;

import static com.bob.global.exception.response.ApplicationError.ALREADY_POST_FAVORITE;
import static com.bob.global.exception.response.ApplicationError.INVALID_POST_FAVORITE;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.post.dto.command.RegisterPostFavoriteCommand;
import com.bob.core.application.post.dto.command.RemovePostFavoriteCommand;
import com.bob.core.domain.post.Post;
import com.bob.core.domain.post.repository.PostRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("게시글 찜 테스트")
@ContainerTest
record PostFavoriteManagerTest(PostFavoriteManager postFavoriteManager, PostRepository postRepository,
                               EntityManager em
) {

    @Test
    void 게시글_찜_등록() {
        Post post = postRepository.save(createPost());
        RegisterPostFavoriteCommand command = new RegisterPostFavoriteCommand(MEMBER_ID);

        postFavoriteManager.registerFavorite(post.getId(), command);
        clearPersistenceContext();

        Post result = postRepository.findById(post.getId()).orElseThrow();
        assertThat(result.isFavorite(MEMBER_ID)).isTrue();
        assertThat(result.getScrapCount()).isEqualTo(1);
    }

    @Test
    void 게시글_찜_등록_시_이미_찜한_경우_예외가_발생한다() {
        Post post = postRepository.save(createPost());
        RegisterPostFavoriteCommand command = new RegisterPostFavoriteCommand(MEMBER_ID);
        postFavoriteManager.registerFavorite(post.getId(), command);

        assertThatThrownBy(() -> postFavoriteManager.registerFavorite(post.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ALREADY_POST_FAVORITE.getMessage());
    }

    @Test
    void 게시글_찜_해제() {
        Post post = postRepository.save(createPost());
        RegisterPostFavoriteCommand registerCommand = new RegisterPostFavoriteCommand(MEMBER_ID);
        postFavoriteManager.registerFavorite(post.getId(), registerCommand);
        clearPersistenceContext();

        RemovePostFavoriteCommand removeCommand = new RemovePostFavoriteCommand(MEMBER_ID);
        postFavoriteManager.removeFavorite(post.getId(), removeCommand);
        clearPersistenceContext();

        Post result = postRepository.findById(post.getId()).orElseThrow();
        assertThat(result.isFavorite(MEMBER_ID)).isFalse();
        assertThat(result.getScrapCount()).isEqualTo(0);
    }

    @Test
    void 게시글_찜_해제_시_찜하지_않은_경우_예외가_발생한다() {
        Post post = postRepository.save(createPost());
        RemovePostFavoriteCommand command = new RemovePostFavoriteCommand(MEMBER_ID);

        assertThatThrownBy(() -> postFavoriteManager.removeFavorite(post.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(INVALID_POST_FAVORITE.getMessage());
    }

    @Test
    void 게시글_찜_여러_회원() {
        Post post = postRepository.save(createPost());
        RegisterPostFavoriteCommand command1 = new RegisterPostFavoriteCommand(MEMBER_ID);
        RegisterPostFavoriteCommand command2 = new RegisterPostFavoriteCommand(OTHER_MEMBER_ID);

        postFavoriteManager.registerFavorite(post.getId(), command1);
        postFavoriteManager.registerFavorite(post.getId(), command2);
        clearPersistenceContext();

        Post result = postRepository.findById(post.getId()).orElseThrow();
        assertThat(result.isFavorite(MEMBER_ID)).isTrue();
        assertThat(result.isFavorite(OTHER_MEMBER_ID)).isTrue();
        assertThat(result.getScrapCount()).isEqualTo(2);
    }

    private void clearPersistenceContext() {
        em.flush();
        em.clear();
    }
}
