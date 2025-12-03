package com.bob.core.application.trade.port.in;

import static com.bob.global.exception.response.ApplicationError.TRADE_REMOVE_ONLY_ABORTED;
import static com.bob.global.exception.response.ApplicationError.TRADE_REMOVE_ONLY_REQUESTER;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.trade.dto.command.ChangeTradeStatusCommand;
import com.bob.core.application.trade.dto.command.CreateTradeCommand;
import com.bob.core.application.trade.dto.command.RemoveTradeCommand;
import com.bob.core.domain.trade.Trade;
import com.bob.core.domain.trade.repository.TradeRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("거래 삭제 테스트")
@ContainerTest
record TradeRemoverTest(TradeRemover tradeRemover, TradeModifier tradeModifier, TradeCreator tradeCreator,
                        TradeRepository tradeRepository
) {

    @Test
    void 거래_삭제() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        Long tradeId = trade.getId();

        ChangeTradeStatusCommand statusCommand = new ChangeTradeStatusCommand(MEMBER_ID, "REJECTED", null);
        tradeModifier.changeStatus(tradeId, statusCommand);

        RemoveTradeCommand command = new RemoveTradeCommand(OTHER_MEMBER_ID);
        tradeRemover.remove(tradeId, command);

        assertThat(tradeRepository.findById(tradeId)).isEmpty();
    }

    @Test
    void 거래_삭제_시_구매자가_아니면_예외_발생() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        RemoveTradeCommand command = new RemoveTradeCommand(MEMBER_ID);

        assertThatThrownBy(() -> tradeRemover.remove(trade.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_REMOVE_ONLY_REQUESTER.getMessage());
    }

    @Test
    void 거래_삭제_시_취소되지_않은_상태면_예외_발생() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(2L, OTHER_MEMBER_ID, List.of(5L), false));
        RemoveTradeCommand command = new RemoveTradeCommand(OTHER_MEMBER_ID);

        assertThatThrownBy(() -> tradeRemover.remove(trade.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_REMOVE_ONLY_ABORTED.getMessage());
    }
}
