package com.bob.core.trade.adapter.api;

import static com.bob.core.trade.domain.status.Status.CANCELED;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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

import com.bob.core.trade.adapter.api.request.ChangeTradeItemsRequest;
import com.bob.core.trade.adapter.api.request.ChangeTradeStatusRequest;
import com.bob.core.trade.adapter.api.request.CreateTradeRequest;
import com.bob.core.trade.adapter.api.response.ChangeTradeStatusResponse;
import com.bob.core.trade.adapter.api.response.CreateTradeResponse;
import com.bob.core.trade.application.dto.command.CreateTradeCommand;
import com.bob.core.trade.application.port.in.TradeCreator;
import com.bob.core.trade.domain.Trade;
import com.bob.core.trade.domain.repository.TradeRepository;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;

@DisplayName("거래 API 테스트")
@BobApiTest
record TradeApiTest(
    MockMvcTester mvcTester, TradeCreator tradeCreator, TradeRepository tradeRepository, ObjectMapper objectMapper
) {

    @Test
    void 거래_생성() throws Exception {
        setAuthentication(OTHER_MEMBER_ID);
        CreateTradeRequest request = new CreateTradeRequest(1L, List.of(5L), false);

        MvcTestResult result = mvcTester.post()
            .uri("/trades")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .exchange();

        assertThat(result).hasStatus(201);

        CreateTradeResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            CreateTradeResponse.class
        );

        assertThat(response.id()).isNotNull();
    }

    @Test
    void 거래_생성_시_거래_품목이_1개_이상_존재하지_않으면_사용자_예외가_발생한다() throws Exception {
        setAuthentication(OTHER_MEMBER_ID);
        CreateTradeRequest request = new CreateTradeRequest(1L, List.of(), false);

        MvcTestResult result = mvcTester.post()
            .uri("/trades")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .exchange();

        assertThat(result).hasStatus4xxClientError();
    }

    @Test
    void 거래_목록_조회() {
        setAuthentication(OTHER_MEMBER_ID);
        tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));

        MvcTestResult result = mvcTester.get()
            .uri("/trades")
            .exchange();

        assertThat(result).hasStatusOk();
    }

    @Test
    void 거래_상세_조회() {
        setAuthentication(OTHER_MEMBER_ID);
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));

        MvcTestResult result = mvcTester.get()
            .uri("/trades/{tradeId}", trade.getId())
            .exchange();

        assertThat(result).hasStatusOk();
    }

    @Test
    void 거래_상태_변경_수락() throws Exception {
        setAuthentication(MEMBER_ID);
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        ChangeTradeStatusRequest request = new ChangeTradeStatusRequest("ACCEPTED", null);

        MvcTestResult result = mvcTester.patch()
            .uri("/trades/{tradeId}", trade.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .exchange();

        assertThat(result).hasStatusOk();

        ChangeTradeStatusResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            ChangeTradeStatusResponse.class
        );

        assertThat(response.chatroomId()).isNotNull();
    }

    @Test
    void 거래_상태_변경_취소() throws Exception {
        setAuthentication(OTHER_MEMBER_ID);
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        ChangeTradeStatusRequest request = new ChangeTradeStatusRequest("CANCELED", "단순 변심");

        MvcTestResult result = mvcTester.patch()
            .uri("/trades/{tradeId}", trade.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .exchange();

        assertThat(result).hasStatusOk();
    }

    @Test
    void 거래_품목_변경() throws Exception {
        setAuthentication(OTHER_MEMBER_ID);
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        ChangeTradeItemsRequest request = new ChangeTradeItemsRequest(List.of(6L));

        MvcTestResult result = mvcTester.patch()
            .uri("/trades/{tradeId}/items", trade.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .exchange();

        assertThat(result).hasStatusOk();
    }

    @Test
    void 거래_품목_변경_시_거래_품목이_1개_이상_존재하지_않으면_사용자_예외가_발생한다() throws Exception {
        setAuthentication(OTHER_MEMBER_ID);
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        ChangeTradeItemsRequest request = new ChangeTradeItemsRequest(List.of());

        MvcTestResult result = mvcTester.patch()
            .uri("/trades/{tradeId}/items", trade.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .exchange();

        assertThat(result).hasStatus4xxClientError();
    }

    @Test
    void 거래_삭제() {
        setAuthentication(OTHER_MEMBER_ID);
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));
        trade.updateStatus(CANCELED);
        tradeRepository.save(trade);

        MvcTestResult result = mvcTester.delete()
            .uri("/trades/{tradeId}", trade.getId())
            .exchange();

        assertThat(result).hasStatusOk();
    }

    @Test
    void 거래_삭제_시_취소된_거래가_아니면_사용자_예외() {
        setAuthentication(OTHER_MEMBER_ID);
        Trade trade = tradeCreator.create(new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(5L), false));

        MvcTestResult result = mvcTester.delete()
            .uri("/trades/{tradeId}", trade.getId())
            .exchange();

        assertThat(result).hasStatus4xxClientError();
    }

    void setAuthentication(UUID memberId) {
        MemberDetails principal = new MemberDetails(memberId, true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
