package com.bob.core.management.application.port.in;

import static com.bob.core.trade.domain.status.Status.ACCEPTED;
import static com.bob.core.trade.domain.status.Status.COMPLETED;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageRequest;

import com.bob.core.management.application.dto.result.ManagementMemberDetail;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.core.trade.domain.Trade;
import com.bob.core.trade.domain.repository.TradeRepository;
import com.bob.core.trade.domain.type.Owner;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.report.domain.ReportFixture;
import com.bob.support.fixture.trade.domain.TradeFixture;

@DisplayName("관리 회원 조회 테스트")
@ContainerTest
record ManagementMemberReaderTest(
    ManagementMemberReader memberReader,
    ReportRepository reportRepository,
    TradeRepository tradeRepository
) {

    @Test
    void 관리_회원_목록_조회() {
        var pageable = PageRequest.of(0, 20);

        var result = memberReader.readAll(null, null, pageable);

        assertThat(result.totalCount()).isNotZero();
        assertThat(result.members()).isNotEmpty();

        // 신고 횟수 검증
        Report report = ReportFixture.createProcessedReport();
        reportRepository.save(report);

        var containsReportCountResult = memberReader.readAll("NICKNAME", "other", pageable);
        assertThat(containsReportCountResult.members().get(0).getReportCount()).isEqualTo(1);
    }

    @Test
    void 관리_회원_상세_조회() {
        Trade sold = tradeRepository.save(TradeFixture.createTrade(3L, OTHER_MEMBER_ID, MEMBER_ID, COMPLETED));
        Trade bought = tradeRepository.save(TradeFixture.createTrade(1L, MEMBER_ID, OTHER_MEMBER_ID, COMPLETED));
        Trade exclude = tradeRepository.save(TradeFixture.createTrade(2L, MEMBER_ID, OTHER_MEMBER_ID, ACCEPTED));
        mapItem(sold, bought, exclude);

        reportRepository.save(ReportFixture.createProcessedReport());
        reportRepository.save(ReportFixture.createReport()); // 조회 결과 제외

        ManagementMemberDetail detail = memberReader.readDetail(OTHER_MEMBER_ID);

        /* 회원 정보 */
        assertThat(detail.member()).isNotNull();
        assertThat(detail.member().getId()).isEqualTo(OTHER_MEMBER_ID);

        /* 활동 정보 */
        // 게시글 작성 활동 : 1개 글 작성 (schema.sql 참고),
        assertThat(detail.activities().post()).isNotNull();
        assertThat(detail.activities().post().count()).isEqualTo(1);

        // 거래 활동(완료된 거래만 포함), OTHER_MEMBER의 거래 완료 2개(판매1, 구매1), 미완료 1개(포함 X)
        assertThat(detail.activities().trade()).isNotNull();
        assertThat(detail.activities().trade().count()).isEqualTo(2);
        assertThat(detail.activities().trade().sold()).contains(sold.getId());
        assertThat(detail.activities().trade().bought()).contains(bought.getId());

        /* 받은 신고 정보 */
        // 채팅 신고
        assertThat(detail.reports().chat()).isNotNull();
        assertThat(detail.reports().chat().count()).isZero();

        // 게시글 신고
        assertThat(detail.reports().post()).isNotNull();
        assertThat(detail.reports().post().count()).isEqualTo(1);
        assertThat(detail.reports().post().reason()).contains("사기/허위");
    }

    private void mapItem(Trade sold, Trade bought, Trade exclude) {
        sold.addItem(3L, Owner.SELLER);
        sold.addItem(4L, Owner.BUYER);

        bought.addItem(1L, Owner.SELLER);
        bought.addItem(5L, Owner.BUYER);

        exclude.addItem(2L, Owner.SELLER);
        exclude.addItem(6L, Owner.BUYER);
    }
}
