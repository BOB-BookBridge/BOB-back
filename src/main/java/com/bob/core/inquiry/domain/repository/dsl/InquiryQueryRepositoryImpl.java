package com.bob.core.inquiry.domain.repository.dsl;

import static com.bob.core.inquiry.domain.QInquiry.inquiry;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.bob.core.inquiry.domain.Inquiry;
import com.bob.core.inquiry.domain.InquiryStatus;
import com.bob.core.inquiry.domain.repository.dsl.query.SearchInquiriesQuery;

@Repository
@RequiredArgsConstructor
public class InquiryQueryRepositoryImpl implements InquiryQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Inquiry> findInquiries(SearchInquiriesQuery query, Pageable pageable) {
        return queryFactory
            .selectFrom(inquiry)
            .where(
                emailContains(query.email()),
                statusEquals(query.status())
            )
            .orderBy(inquiry.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public Long countInquiries(SearchInquiriesQuery query) {
        return queryFactory
            .select(inquiry.count())
            .from(inquiry)
            .where(
                emailContains(query.email()),
                statusEquals(query.status())
            )
            .fetchOne();
    }

    private BooleanExpression emailContains(String email) {
        return email != null ? inquiry.email.containsIgnoreCase(email) : null;
    }

    private BooleanExpression statusEquals(InquiryStatus status) {
        return status != null ? inquiry.status.eq(status) : null;
    }
}
