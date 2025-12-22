package com.bob.core.trade.application.port.in;

import static com.bob.core.trade.domain.status.Status.ACCEPTED;
import static com.bob.core.trade.domain.status.Status.CANCELED;
import static com.bob.core.trade.domain.status.Status.COMPLETED;
import static com.bob.core.trade.domain.status.Status.REJECTED;
import static com.bob.core.trade.domain.status.Status.REQUESTED;
import static com.bob.core.trade.domain.status.Status.RESERVED;
import static com.bob.core.trade.domain.type.Owner.BUYER;
import static com.bob.global.exception.response.ApplicationError.NO_CHANGES;
import static com.bob.global.exception.response.ApplicationError.TRADE_ACCESS_DENIED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_PROCESSED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ITEM_UNCHANGEABLE;
import static com.bob.global.exception.response.ApplicationError.TRADE_MAIN_ITEM_UNCHANGEABLE;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_ALREADY_ABORTED;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_ALREADY_COMPLETED;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_NOT_CHANGEABLE;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_UNCHANGED;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MOCK_MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.PostRepository;
import com.bob.core.post.domain.status.TradeProgress;
import com.bob.core.trade.application.dto.command.ChangeTradeItemsCommand;
import com.bob.core.trade.application.dto.command.ChangeTradeStatusCommand;
import com.bob.core.trade.application.dto.command.CreateTradeCommand;
import com.bob.core.trade.application.dto.result.ChangeTradeStatusResult;
import com.bob.core.trade.domain.Trade;
import com.bob.core.trade.domain.repository.TradeRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("거래 수정 테스트")
@ContainerTest
record TradeModifierTest(
    TradeModifier tradeModifier, TradeCreator tradeCreator, TradeRepository tradeRepository,
    PostRepository postRepository, EntityManager em
) {

    @Test
    void 거래_상태_변경_수락() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        Long tradeId = trade.getId();
        var command = new ChangeTradeStatusCommand(MEMBER_ID, "ACCEPTED", null);

        ChangeTradeStatusResult result = tradeModifier.changeStatus(tradeId, command);

        Trade updated = tradeRepository.findById(tradeId).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(ACCEPTED);
        assertThat(result.chatroomId()).isNotNull();
    }

    @Test
    void 거래_상태_변경_거절() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        Long tradeId = trade.getId();
        var command = new ChangeTradeStatusCommand(MEMBER_ID, "REJECTED", null);

        ChangeTradeStatusResult result = tradeModifier.changeStatus(tradeId, command);

        Trade updated = tradeRepository.findById(tradeId).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(REJECTED);
        assertThat(result.chatroomId()).isNull();
    }

    @Test
    void 거래_상태_변경_취소() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(2L, OTHER_MEMBER_ID, List.of(5L), false));
        var command = new ChangeTradeStatusCommand(OTHER_MEMBER_ID, "CANCELED", "더 이상 필요 없음");

        ChangeTradeStatusResult result = tradeModifier.changeStatus(trade.getId(), command);

        assertThat(result.trade().getStatus()).isEqualTo(CANCELED);
        assertThat(result.chatroomId()).isNull();
    }

    @Test
    void 거래_상태_변경_완료() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(2L, OTHER_MEMBER_ID, List.of(5L), false));
        var command = new ChangeTradeStatusCommand(MEMBER_ID, "COMPLETED", null);

        ChangeTradeStatusResult result = tradeModifier.changeStatus(trade.getId(), command);

        assertThat(result.trade().getStatus()).isEqualTo(COMPLETED);
        assertThat(result.chatroomId()).isNull();
    }

    @Test
    void 거래_상태_변경_시_구매자는_취소만_가능() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(2L, OTHER_MEMBER_ID, List.of(5L), false));
        var command = new ChangeTradeStatusCommand(OTHER_MEMBER_ID, "CANCELED", null);

        ChangeTradeStatusResult result = tradeModifier.changeStatus(trade.getId(), command);

        assertThat(result.trade().getStatus()).isEqualTo(CANCELED);
        assertThat(result.chatroomId()).isNull();
    }

    @Test
    void 거래_상태_변경_시_대기_상태에서_예약_상태로_변경되면_게시물_거래_상태가_예약_상태로_변경() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        var reserveCommand = new ChangeTradeStatusCommand(MEMBER_ID, "RESERVED", null);
        tradeModifier.changeStatus(trade.getId(), reserveCommand);

        Post after = postRepository.findById(trade.getPostId()).get();
        assertThat(after.getTradeProgress()).isEqualTo(TradeProgress.RESERVED);
    }

    @Test
    void 거래_상태_변경_시_예약_상태에서_완료_상태로_변경되면_게시물_거래_상태가_완료_상태로_변경() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        var reserveCommand = new ChangeTradeStatusCommand(MEMBER_ID, "COMPLETED", null);
        tradeModifier.changeStatus(trade.getId(), reserveCommand);

        Post after = postRepository.findById(trade.getPostId()).get();
        assertThat(after.getTradeProgress()).isEqualTo(TradeProgress.COMPLETED);
    }

    @Test
    void 거래_상태_변경_시_예약_상태에서_이전_상태로_변경되면_게시물_거래_상태가_대기_상태로_변경() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        var reserveCommand = new ChangeTradeStatusCommand(MEMBER_ID, "RESERVED", null);
        tradeModifier.changeStatus(trade.getId(), reserveCommand);

        Post before = postRepository.findById(trade.getPostId()).get();
        assertThat(before.getTradeProgress()).isEqualTo(TradeProgress.RESERVED);

        var acceptCommand = new ChangeTradeStatusCommand(MEMBER_ID, "ACCEPTED", null);
        tradeModifier.changeStatus(trade.getId(), acceptCommand);

        em.flush();
        em.clear();

        Post after = postRepository.findById(trade.getPostId()).get();
        assertThat(after.getTradeProgress()).isEqualTo(TradeProgress.READY);
    }

    @Test
    void 거래_상태_변경_시_참여자가_아니면_사용자_예외가_발생한다() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        var command = new ChangeTradeStatusCommand(java.util.UUID.randomUUID(), "ACCEPTED", null);

        assertThatThrownBy(() -> tradeModifier.changeStatus(trade.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_ACCESS_DENIED.getMessage());
    }

    @Test
    void 거래_상태_변경_시_판매자가_아니면_사용자_예외가_발생한다() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        var command = new ChangeTradeStatusCommand(OTHER_MEMBER_ID, "ACCEPTED", null);

        assertThatThrownBy(() -> tradeModifier.changeStatus(trade.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_ACCESS_DENIED.getMessage());
    }

    @Test
    void 거래_상태_변경_시_이미_예약된_거래가_있으면_사용자_예외가_발생한다() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, MOCK_MEMBER_ID, List.of(8L), false));
        var reserveCommand = new ChangeTradeStatusCommand(MEMBER_ID, "RESERVED", null);
        tradeModifier.changeStatus(trade.getId(), reserveCommand);

        Trade afterTrade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(7L), false));
        var duplicateReserveCommand = new ChangeTradeStatusCommand(MEMBER_ID, "RESERVED", null);

        assertThatThrownBy(() -> tradeModifier.changeStatus(afterTrade.getId(), duplicateReserveCommand))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_ALREADY_PROCESSED.getMessage());
    }

    @Test
    void 거래_상태_변경_시_요청_상태가_이전과_동일하면_사용자_예외가_발생한다() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        trade.updateStatus(RESERVED);

        var command = new ChangeTradeStatusCommand(MEMBER_ID, "RESERVED", null);

        assertThatThrownBy(() -> tradeModifier.changeStatus(trade.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_STATUS_UNCHANGED.getMessage());
    }

    @Test
    void 거래_상태_변경_시_완료된_거래라면_사용자_예외가_발생한다() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        trade.updateStatus(COMPLETED);
        var command = new ChangeTradeStatusCommand(MEMBER_ID, "ACCEPTED", null);

        assertThatThrownBy(() -> tradeModifier.changeStatus(trade.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_STATUS_ALREADY_COMPLETED.getMessage());
    }

    @Test
    void 거래_상태_변경_시_대기_상태로_변경하면_사용자_예외가_발생한다() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        tradeModifier.changeStatus(trade.getId(), new ChangeTradeStatusCommand(MEMBER_ID, "ACCEPTED", null));
        var command = new ChangeTradeStatusCommand(MEMBER_ID, "REQUESTED", null);

        assertThatThrownBy(() -> tradeModifier.changeStatus(trade.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_STATUS_NOT_CHANGEABLE.getMessage());
    }

    @Test
    void 거래_상태_변경_시_판매자의_임의로_취소_및_거절된_거래의_상태를_변경하면_사용자_예외가_발생한다() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        trade.updateStatus(REJECTED);
        var command1 = new ChangeTradeStatusCommand(MEMBER_ID, "ACCEPTED", null);

        assertThatThrownBy(() -> tradeModifier.changeStatus(trade.getId(), command1))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_STATUS_ALREADY_ABORTED.getMessage());

        trade.updateStatus(CANCELED);
        var command2 = new ChangeTradeStatusCommand(MEMBER_ID, "ACCEPTED", null);
        assertThatThrownBy(() -> tradeModifier.changeStatus(trade.getId(), command2))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_STATUS_ALREADY_ABORTED.getMessage());

        var command3 = new ChangeTradeStatusCommand(MEMBER_ID, "RESERVED", null);
        assertThatThrownBy(() -> tradeModifier.changeStatus(trade.getId(), command3))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_STATUS_ALREADY_ABORTED.getMessage());

        var command4 = new ChangeTradeStatusCommand(MEMBER_ID, "COMPLETED", null);
        assertThatThrownBy(() -> tradeModifier.changeStatus(trade.getId(), command4))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_STATUS_ALREADY_ABORTED.getMessage());
    }

    @Test
    void 거래_품목_변경() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        var command = new ChangeTradeItemsCommand(List.of(6L), OTHER_MEMBER_ID);

        Trade changed = tradeModifier.changeItems(trade.getId(), command);

        List<Long> buyerItems = changed.getItemIdsByOwner(BUYER);
        assertThat(buyerItems).containsExactly(6L);
    }

    @Test
    void 거래_품목_변경_시_취소_거절_상태였다면_대기_상태로_변경() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        trade.updateStatus(CANCELED);
        var command = new ChangeTradeItemsCommand(List.of(6L), OTHER_MEMBER_ID);

        Trade updated = tradeModifier.changeItems(trade.getId(), command);

        assertThat(updated.getStatus()).isEqualTo(REQUESTED);
    }

    @Test
    void 거래_품목_변경_시_판매자의_대표_품목이_누락되면_사용자_예외가_발생한다() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));

        var command = new ChangeTradeItemsCommand(List.of(4L), MEMBER_ID); // 대표 품목(id = 1) 누락

        assertThatThrownBy(() -> tradeModifier.changeItems(trade.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_MAIN_ITEM_UNCHANGEABLE.getMessage());
    }

    @Test
    void 거래_품목_변경_시_동일한_품목이면_사용자_예외가_발생한다() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        var command = new ChangeTradeItemsCommand(List.of(5L), OTHER_MEMBER_ID);

        assertThatThrownBy(() -> tradeModifier.changeItems(trade.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(NO_CHANGES.getMessage());
    }

    @Test
    void 거래_품목_변경_시_예약_완료_상태의_거래라면_사용자_예외가_발생한다() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        trade.updateStatus(RESERVED);

        var command1 = new ChangeTradeItemsCommand(List.of(5L), OTHER_MEMBER_ID);

        assertThatThrownBy(() -> tradeModifier.changeItems(trade.getId(), command1))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_ITEM_UNCHANGEABLE.getMessage());

        trade.updateStatus(COMPLETED);

        var command2 = new ChangeTradeItemsCommand(List.of(5L), OTHER_MEMBER_ID);

        assertThatThrownBy(() -> tradeModifier.changeItems(trade.getId(), command2))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_ITEM_UNCHANGEABLE.getMessage());
    }
}
