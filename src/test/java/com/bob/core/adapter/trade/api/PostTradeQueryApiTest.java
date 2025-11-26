package com.bob.core.adapter.trade.api;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MOCK_MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.bob.core.application.trade.dto.command.CreateTradeCommand;
import com.bob.core.application.trade.port.in.TradeCreator;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;

@DisplayName("게시물 거래 조회 API 테스트")
@BobApiTest
record PostTradeQueryApiTest(MockMvcTester mvcTester, TradeCreator tradeCreator) {

    @Test
    void 게시물_거래_목록_조회() {
        setAuthentication(MEMBER_ID);
        Long postId = 1L;
        tradeCreator.create(new CreateTradeCommand(postId, OTHER_MEMBER_ID, List.of(5L), false));
        tradeCreator.create(new CreateTradeCommand(postId, MOCK_MEMBER_ID, List.of(8L), false));

        MvcTestResult result = mvcTester.get()
            .uri("/posts/{postId}/trades", postId)
            .exchange();

        assertThat(result).hasStatusOk();
    }

    @Test
    void 게시물_거래_목록_조회_판매자() {
        setAuthentication(MEMBER_ID);
        Long postId = 1L;
        tradeCreator.create(new CreateTradeCommand(postId, OTHER_MEMBER_ID, List.of(5L), false));

        MvcTestResult result = mvcTester.get()
            .uri("/posts/{postId}/trades", postId)
            .exchange();

        assertThat(result).hasStatusOk();
    }

    @Test
    void 게시물_거래_목록_조회_시_구매자가_조회하면_사용자_예외가_발생한다() {
        setAuthentication(OTHER_MEMBER_ID);
        Long postId = 1L;
        tradeCreator.create(new CreateTradeCommand(postId, OTHER_MEMBER_ID, List.of(5L), false));

        MvcTestResult result = mvcTester.get()
            .uri("/posts/{postId}/trades", postId)
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
