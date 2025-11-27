package com.bob.core.application.trade.port.in;

import static com.bob.global.exception.response.ApplicationError.TRADE_ACCESS_DENIED;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageRequest;

import com.bob.core.application.trade.dto.command.CreateTradeCommand;
import com.bob.core.application.trade.dto.query.ReadPostTradeQuery;
import com.bob.core.application.trade.dto.query.ReadPostTradesQuery;
import com.bob.core.application.trade.dto.query.ReadTradeDetailQuery;
import com.bob.core.application.trade.dto.query.ReadTradeStatusMapQuery;
import com.bob.core.application.trade.dto.result.TradeDetail;
import com.bob.core.application.trade.dto.result.TradeSummaries;
import com.bob.core.application.trade.dto.result.internal.PostTrade;
import com.bob.core.domain.trade.Trade;
import com.bob.core.domain.trade.repository.dsl.query.ReadTradesQuery;
import com.bob.core.domain.trade.repository.dsl.query.SearchKey;
import com.bob.core.domain.trade.status.Status;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("거래 조회 테스트")
@ContainerTest
record TradeReaderTest(TradeReader tradeReader, TradeCreator tradeCreator) {

    @Test
    void 게시글_거래_목록_조회() {
        tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));

        Long postId = 1L;
        ReadPostTradesQuery query = new ReadPostTradesQuery(postId, MEMBER_ID);

        List<PostTrade> response = tradeReader.readPostTradeSummaries(query);

        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).buyer().nickname()).isEqualTo("other");
    }

    @Test
    void 게시글_거래_목록_조회_시_작성자가_아니면_예외_발생() {
        Long postId = 1L;
        ReadPostTradesQuery query = new ReadPostTradesQuery(postId, OTHER_MEMBER_ID);

        assertThatThrownBy(() -> tradeReader.readPostTradeSummaries(query))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_ACCESS_DENIED.getMessage());
    }

    @Test
    void 게시글_거래_조회() {
        Long postId = 1L;
        tradeCreator.create(new CreateTradeCommand(postId, OTHER_MEMBER_ID, List.of(5L), false));

        var query = new ReadPostTradeQuery(postId, OTHER_MEMBER_ID);

        Optional<Trade> found = tradeReader.readByPostAndMember(query);
        assertThat(found).isPresent();
        assertThat(found.get().getPostId()).isEqualTo(postId);
        assertThat(found.get().getBuyerId()).isEqualTo(OTHER_MEMBER_ID);
    }

    @Test
    void 거래_목록_조회_ALL() {
        tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        tradeCreator.create(new CreateTradeCommand(2L, OTHER_MEMBER_ID, List.of(6L), false));

        ReadTradesQuery query = new ReadTradesQuery(MEMBER_ID, SearchKey.ALL, null);

        TradeSummaries response = tradeReader.readTrades(query, PageRequest.of(0, 10));

        assertThat(response).isNotNull();
        assertThat(response.totalCount()).isEqualTo(2L);
        assertThat(response.trades()).hasSize(2);
    }

    @Test
    void 모든_거래_목록_조회() {
        tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));

        ReadTradesQuery query = new ReadTradesQuery(MEMBER_ID, SearchKey.ALL, List.of(Status.REQUESTED));

        TradeSummaries response = tradeReader.readTrades(query, PageRequest.of(0, 10));

        assertThat(response).isNotNull();
        assertThat(response.totalCount()).isEqualTo(1L);
    }

    @Test
    void 보낸_거래_목록_조회() {
        tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        tradeCreator.create(new CreateTradeCommand(2L, OTHER_MEMBER_ID, List.of(6L), false));

        ReadTradesQuery query = new ReadTradesQuery(OTHER_MEMBER_ID, SearchKey.SENT, null);

        TradeSummaries response = tradeReader.readTrades(query, PageRequest.of(0, 10));

        assertThat(response).isNotNull();
        assertThat(response.totalCount()).isEqualTo(2L);
    }

    @Test
    void 받은_거래_목록_조회() {
        tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        tradeCreator.create(new CreateTradeCommand(2L, OTHER_MEMBER_ID, List.of(6L), false));

        ReadTradesQuery query = new ReadTradesQuery(MEMBER_ID, SearchKey.RECEIVED, null);

        TradeSummaries response = tradeReader.readTrades(query, PageRequest.of(0, 10));

        assertThat(response).isNotNull();
        assertThat(response.totalCount()).isEqualTo(2L);
    }

    @Test
    void 거래_상세_조회() {
        Long tradeId = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false)).getId();

        ReadTradeDetailQuery query = new ReadTradeDetailQuery(MEMBER_ID);

        TradeDetail response = tradeReader.readTradeDetail(tradeId, query);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(tradeId);
        assertThat(response.seller().item()).hasSize(1);
        assertThat(response.buyer().item()).hasSize(1);
    }

    @Test
    void 거래_조회_시_참여자가_아니면_예외_발생() {
        Long tradeId = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false)).getId();

        UUID randomMemberId = UUID.randomUUID();
        ReadTradeDetailQuery query = new ReadTradeDetailQuery(randomMemberId);

        assertThatThrownBy(() -> tradeReader.readTradeDetail(tradeId, query))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TRADE_ACCESS_DENIED.getMessage());
    }

    @Test
    void 거래_상태_조회() {
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));

        String status = tradeReader.readTradeStatus(trade.getId());

        assertThat(status).isEqualTo("REQUESTED");
    }

    @Test
    void 거래_상태_조회_시_존재하지_않으면_REMOVED_반환() {
        Long nonExistTradeId = 10000L;

        String status = tradeReader.readTradeStatus(nonExistTradeId);

        assertThat(status).isEqualTo("REMOVED");
    }

    @Test
    void 거래_상태_맵_조회() {
        tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        tradeCreator.create(new CreateTradeCommand(2L, OTHER_MEMBER_ID, List.of(6L), false));

        ReadTradeStatusMapQuery query = new ReadTradeStatusMapQuery(OTHER_MEMBER_ID, List.of(1L, 2L, 3L));

        Map<Long, String> statusMap = tradeReader.readTradeStatusMap(query);

        assertThat(statusMap).hasSize(2);
        assertThat(statusMap.get(1L)).isEqualTo("REQUESTED");
        assertThat(statusMap.get(2L)).isEqualTo("REQUESTED");
    }
}
