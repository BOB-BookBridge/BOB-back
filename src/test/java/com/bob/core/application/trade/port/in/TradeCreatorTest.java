package com.bob.core.application.trade.port.in;

import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_PROCESSED;
import static com.bob.global.exception.response.ApplicationError.TRADE_POST_REMOVED;
import static com.bob.global.exception.response.ApplicationError.TRADE_SELF_NOT_ALLOWED;
import static com.bob.global.exception.response.ApplicationError.TRADE_SELLER_WISH_NOT_MATCH;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MOCK_MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.trade.dto.command.CreateTradeCommand;
import com.bob.core.domain.post.Post;
import com.bob.core.domain.post.repository.PostRepository;
import com.bob.core.domain.post.status.TradeProgress;
import com.bob.core.domain.trade.Trade;
import com.bob.core.domain.trade.repository.TradeRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("거래 생성 테스트")
@ContainerTest
record TradeCreatorTest(
    TradeCreator tradeCreator, TradeRepository tradeRepository, PostRepository postRepository, EntityManager em
) {

    @Test
    void 거래_생성() {
        Long postId = 1L;
        Long buyerItemId = 5L;

        CreateTradeCommand command = new CreateTradeCommand(postId, OTHER_MEMBER_ID, List.of(buyerItemId), false);

        Trade trade = tradeCreator.create(command);
        assertThat(trade.getId()).isNotNull();
        assertThat(trade.getPostId()).isEqualTo(postId);
        assertThat(trade.getSellerId()).isEqualTo(MEMBER_ID);
        assertThat(trade.getBuyerId()).isEqualTo(OTHER_MEMBER_ID);
        assertThat(trade.getItems()).hasSize(2); // seller 1개 + buyer 1개
    }

    @Test
    void 거래_생성_시_희망_도서만_제안받기_옵션_적용_게시글() {
        Long postId = 3L;
        Long wishItemId = 8L; // OTHER_MEMBER의 희망 도서

        CreateTradeCommand command = new CreateTradeCommand(postId, MOCK_MEMBER_ID, List.of(wishItemId), false);

        Trade trade = tradeCreator.create(command);

        assertThat(trade.getId()).isNotNull();
        assertThat(trade.getPostId()).isEqualTo(postId);
        assertThat(trade.getSellerId()).isEqualTo(OTHER_MEMBER_ID);
        assertThat(trade.getBuyerId()).isEqualTo(MOCK_MEMBER_ID);
    }

    @Test
    void 거래_생성_시_이미_존재하면_기존_거래_반환() {
        Long postId = 1L;
        Long buyerItemId = 5L;

        CreateTradeCommand command1 = new CreateTradeCommand(postId, OTHER_MEMBER_ID, List.of(buyerItemId), false);
        Trade first = tradeCreator.create(command1);

        CreateTradeCommand command2 = new CreateTradeCommand(postId, OTHER_MEMBER_ID, List.of(buyerItemId), false);
        Trade second = tradeCreator.create(command2);

        assertThat(second.getId()).isEqualTo(first.getId());
    }

    @Test
    void 거래_생성_시_자신의_게시글이라면_사용자_예외가_발생한다() {
        Long postId = 1L;
        Long myItemId = 4L;

        CreateTradeCommand command = new CreateTradeCommand(postId, MEMBER_ID, List.of(myItemId), false);

        assertThatThrownBy(() -> tradeCreator.create(command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_SELF_NOT_ALLOWED.getMessage());
    }

    @Test
    void 거래_생성_시_삭제된_게시글이면_사용자_예외가_발생한다() {
        Long postId = 4L;
        Long buyerItemId = 5L;

        CreateTradeCommand command = new CreateTradeCommand(postId, OTHER_MEMBER_ID, List.of(buyerItemId), false);

        assertThatThrownBy(() -> tradeCreator.create(command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_POST_REMOVED.getMessage());
    }

    @Test
    void 거래_생성_시_이미_거래_완료된_게시글이면_사용자_예외가_발생한다() {
        Long postId = 5L;
        Post post = postRepository.findById(postId).get();
        post.updateTradeProgress(TradeProgress.COMPLETED);
        postRepository.save(post);

        em.flush();
        em.clear();

        CreateTradeCommand command = new CreateTradeCommand(postId, MOCK_MEMBER_ID, List.of(8L), false);

        assertThatThrownBy(() -> tradeCreator.create(command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_ALREADY_PROCESSED.getMessage());
    }

    @Test
    void 거래_생성_시_희망_도서만_제안받기_옵션이_적용된_게시글이고_거래_요청_품목에_희망_도서가_포함되지_않았다면_사용자_예외가_발생한다() {
        Long postId = 3L;
        Long nonWishItemId = 5L;

        CreateTradeCommand command = new CreateTradeCommand(postId, MOCK_MEMBER_ID, List.of(nonWishItemId), false);

        assertThatThrownBy(() -> tradeCreator.create(command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_SELLER_WISH_NOT_MATCH.getMessage());
    }
}
