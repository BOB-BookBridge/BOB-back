package com.bob.core.report.domain.repository.dsl;

import static com.bob.core.report.domain.QReport.report;

import java.util.List;
import java.util.UUID;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.ReportStatus;
import com.bob.core.report.domain.ReportTarget;
import com.bob.core.report.domain.repository.dsl.query.SearchReportsQuery;

@Repository
@RequiredArgsConstructor
public class ReportQueryRepositoryImpl implements ReportQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Report> findReports(SearchReportsQuery query, Pageable pageable) {
        return queryFactory
            .selectFrom(report)
            .where(
                reporterIdCondition(query.reporterId()),
                reportedIdCondition(query.reportedId()),
                targetCondition(query.target()),
                statusCondition(query.status())
            )
            .orderBy(
                report.status.when(ReportStatus.PENDING).then(0).otherwise(1).asc(),
                report.id.desc()
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public Long countReports(SearchReportsQuery query) {
        return queryFactory
            .select(report.count())
            .from(report)
            .where(
                reporterIdCondition(query.reporterId()),
                reportedIdCondition(query.reportedId()),
                targetCondition(query.target()),
                statusCondition(query.status())
            )
            .fetchOne();
    }

    private BooleanExpression reporterIdCondition(UUID reporterId) {
        return reporterId != null ? report.reporterId.eq(reporterId) : null;
    }

    private BooleanExpression reportedIdCondition(UUID reportedId) {
        return reportedId != null ? report.reportedId.eq(reportedId) : null;
    }

    private BooleanExpression targetCondition(ReportTarget target) {
        return target != null ? report.target.eq(target) : null;
    }

    private BooleanExpression statusCondition(ReportStatus status) {
        return status != null ? report.status.eq(status) : null;
    }
}
