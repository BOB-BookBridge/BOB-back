package com.bob.core.post.application.port.in;

import static com.bob.core.post.domain.status.Status.ACTIVE;
import static com.bob.core.post.domain.status.Status.PENDING;
import static com.bob.core.post.domain.status.TradeProgress.READY;
import static com.bob.global.exception.response.ApplicationError.POST_VERIFIED_AREA_REQUIRED;
import static com.bob.support.fixture.area.domain.AreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.post.dto.command.CreatePostCommandFixture.createPostCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.repository.MemberRepository;
import com.bob.core.post.application.dto.command.CreatePostCommand;
import com.bob.core.post.domain.Post;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.member.domain.MemberFixture;

@DisplayName("게시물 생성 테스트")
@ContainerTest
record PostCreatorTest(PostCreator postCreator, MemberRepository memberRepository) {

    @Test
    void 게시물_생성() {
        String description = null;
        CreatePostCommand command = createPostCommand(description);

        Post post = postCreator.create(command);

        assertThat(post.getId()).isNotNull();
        assertThat(post.getStatus()).isEqualTo(ACTIVE);
        assertThat(post.getWriterId()).isEqualTo(MEMBER_ID);
        assertThat(post.getCategoryId()).isEqualTo(command.categoryId());
        assertThat(post.getTitle()).isEqualTo(command.bookTitle());
        assertThat(post.getDescription()).isEqualTo(command.description());
        assertThat(post.getThumbnailUrl()).isEqualTo(command.bookCover());
        assertThat(post.getPrice()).isEqualTo(command.bookPriceStandard());
        assertThat(post.getTradeProgress()).isEqualTo(READY);
        assertThat(post.getRegistrationAreaId()).isEqualTo(EMD_AREA_ID);
        assertThat(post.getViewCount()).isEqualTo(0);
        assertThat(post.getScrapCount()).isEqualTo(0);
        assertThat(post.getCreatedAt()).isNotNull();
    }

    @Test
    void 금지_키워드_포함_게시물_생성() {
        CreatePostCommand command = createPostCommand("비속어");

        Post post = postCreator.create(command);

        assertThat(post.getId()).isNotNull();
        assertThat(post.getStatus()).isEqualTo(PENDING);
        assertThat(post.getFilterWords().get(0)).isEqualTo("비속어");
    }

    @Test
    void 게시물_생성시_지역_인증이_되지_않았다면_사용자_예외가_발생한다() {
        Member unauthenticatedMember = MemberFixture.createUnauthenticatedMember();
        memberRepository.save(unauthenticatedMember);

        CreatePostCommand command = createPostCommand(unauthenticatedMember.getId());

        assertThatThrownBy(() -> postCreator.create(command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(POST_VERIFIED_AREA_REQUIRED.getMessage());
    }
}
