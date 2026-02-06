package com.bob.core.post.application.port.in;

import static com.bob.core.post.domain.status.Status.ACTIVE;
import static com.bob.core.post.domain.status.Status.DEACTIVATED;
import static com.bob.core.post.domain.status.Status.PENDING;
import static com.bob.core.post.domain.status.TradeProgress.COMPLETED;
import static com.bob.core.post.domain.status.TradeProgress.READY;
import static com.bob.core.post.domain.status.TradeProgress.RESERVED;
import static com.bob.global.exception.response.ApplicationError.POST_OWNER_REQUIRED;
import static com.bob.global.exception.response.ApplicationError.POST_UNREMOVABLE_STATE;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.post.application.dto.command.ChangeMemberPostStatusCommand;
import com.bob.core.post.application.dto.command.ChangePostInfoCommand;
import com.bob.core.post.application.dto.command.ChangePostTradeProgressCommand;
import com.bob.core.post.application.dto.command.RemovePostCommand;
import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.PostRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("게시글 수정 테스트")
@ContainerTest
record PostModifierTest(PostModifier postModifier, PostRepository postRepository, EntityManager em) {

    @Test
    void 게시글_정보_수정() {
        Post post = postRepository.save(createPost());
        ChangePostInfoCommand command = new ChangePostInfoCommand(MEMBER_ID, "BEST", "새로운 설명", true);

        Post result = postModifier.changePostInfo(post.getId(), command);

        assertThat(result.getStatus()).isEqualTo(ACTIVE);
        assertThat(result.getBookStatus().name()).isEqualTo("BEST");
        assertThat(result.getDescription()).isEqualTo("새로운 설명");
        assertThat(result.isWishOnly()).isTrue();
    }

    @Test
    void 금지_키워드_포함_게시글_정보_수정() {
        Post post = postRepository.save(createPost());
        ChangePostInfoCommand command = new ChangePostInfoCommand(MEMBER_ID, "BEST", "비속어", true);

        Post result = postModifier.changePostInfo(post.getId(), command);

        assertThat(result.getStatus()).isEqualTo(PENDING);
        assertThat(result.getDescription()).isEqualTo("비속어");
    }

    @Test
    void 게시글_정보_수정_시_작성자가_아니면_예외가_발생한다() {
        Post post = postRepository.save(createPost());
        UUID otherMemberId = UUID.randomUUID();
        ChangePostInfoCommand command = new ChangePostInfoCommand(otherMemberId, "BEST", "새로운 설명", true);

        assertThatThrownBy(() -> postModifier.changePostInfo(post.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(POST_OWNER_REQUIRED.getMessage());
    }

    @Test
    void 게시글_거래_진행_상태_수정() {
        Post post = postRepository.save(createPost());
        ChangePostTradeProgressCommand command = new ChangePostTradeProgressCommand("RESERVED");

        Post result = postModifier.changePostTradeProgress(post.getId(), command);

        assertThat(result.getTradeProgress()).isEqualTo(RESERVED);
    }

    @Test
    void 게시글_거래_진행_상태를_완료로_수정() {
        Post post = postRepository.save(createPost());
        ChangePostTradeProgressCommand command = new ChangePostTradeProgressCommand("COMPLETED");

        Post result = postModifier.changePostTradeProgress(post.getId(), command);

        assertThat(result.getTradeProgress()).isEqualTo(COMPLETED);
    }

    @Test
    void 게시글_거래_진행_상태를_준비로_수정() {
        Post post = postRepository.save(createPost());
        post.updateTradeProgress(RESERVED);
        clearPersistenceContext();

        ChangePostTradeProgressCommand command = new ChangePostTradeProgressCommand("READY");

        Post result = postModifier.changePostTradeProgress(post.getId(), command);

        assertThat(result.getTradeProgress()).isEqualTo(READY);
    }

    @Test
    void 게시글_활성화() {
        Post post = postRepository.save(createPost());
        post.deactivate();
        clearPersistenceContext();

        Post result = postModifier.activate(post.getId());

        assertThat(result.getStatus()).isEqualTo(ACTIVE);
    }

    @Test
    void 게시글_비활성화() {
        Post post = postRepository.save(createPost());
        RemovePostCommand command = new RemovePostCommand(MEMBER_ID);

        Post result = postModifier.deactivate(post.getId(), command);

        assertThat(result.getStatus()).isEqualTo(DEACTIVATED);
        assertThat(result.getFavorites()).isEmpty();
    }

    @Test
    void 게시글_비활성화_시_작성자가_아니면_예외가_발생한다() {
        Post post = postRepository.save(createPost());
        RemovePostCommand command = new RemovePostCommand(OTHER_MEMBER_ID);

        assertThatThrownBy(() -> postModifier.deactivate(post.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(POST_OWNER_REQUIRED.getMessage());
    }

    @Test
    void 게시글_비활성화_시_예약된_게시글이면_예외가_발생한다() {
        Post post = postRepository.save(createPost());
        post.updateTradeProgress(RESERVED);
        RemovePostCommand command = new RemovePostCommand(MEMBER_ID);

        assertThatThrownBy(() -> postModifier.deactivate(post.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(POST_UNREMOVABLE_STATE.getMessage());
    }

    @Test
    void 회원_계정_상태_변경에_따른_게시글_상태_변경_비활성화() {
        Post post1 = postRepository.save(createPost());
        Post post2 = postRepository.save(createPost());
        ChangeMemberPostStatusCommand command = new ChangeMemberPostStatusCommand(MEMBER_ID, DEACTIVATED);

        postModifier.changeStatusByAccountEvent(command);
        clearPersistenceContext();

        Post result1 = postRepository.findById(post1.getId()).orElseThrow();
        Post result2 = postRepository.findById(post2.getId()).orElseThrow();
        assertThat(result1.getStatus()).isEqualTo(DEACTIVATED);
        assertThat(result2.getStatus()).isEqualTo(DEACTIVATED);
    }

    @Test
    void 회원_계정_상태_변경에_따른_게시글_상태_변경_활성화() {
        Post post1 = postRepository.save(createPost());
        Post post2 = postRepository.save(createPost());
        post1.deactivate();
        post2.deactivate();
        clearPersistenceContext();

        ChangeMemberPostStatusCommand command = new ChangeMemberPostStatusCommand(MEMBER_ID, ACTIVE);

        postModifier.changeStatusByAccountEvent(command);
        clearPersistenceContext();

        Post result1 = postRepository.findById(post1.getId()).orElseThrow();
        Post result2 = postRepository.findById(post2.getId()).orElseThrow();
        assertThat(result1.getStatus()).isEqualTo(ACTIVE);
        assertThat(result2.getStatus()).isEqualTo(ACTIVE);
    }

    private void clearPersistenceContext() {
        em.flush();
        em.clear();
    }
}
