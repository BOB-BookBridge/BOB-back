package com.bob.core.member.domain.repository.dsl;

import static com.bob.core.member.domain.QMember.member;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.Role;
import com.bob.core.member.domain.repository.dsl.query.SearchKey;
import com.bob.core.member.domain.repository.dsl.query.SearchMembersQuery;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findMembers(SearchMembersQuery query, Pageable pageable) {
        return queryFactory
            .selectFrom(member)
            .where(searchCondition(query.key(), query.keyword()))
            .orderBy(new CaseBuilder()
                    .when(member.role.eq(Role.ADMIN)).then(1)
                    .when(member.role.eq(Role.USER)).then(2)
                    .otherwise(3)
                    .asc(),
                member.createdAt.desc()
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public Long countMembers(SearchMembersQuery query) {
        return queryFactory
            .select(member.count())
            .from(member)
            .where(searchCondition(query.key(), query.keyword()))
            .fetchOne();
    }

    private BooleanExpression searchCondition(SearchKey searchKey, String keyword) {
        if (keyword == null || keyword.isEmpty())
            return null;

        if (searchKey == null)
            return null;

        return switch (searchKey) {
            case EMAIL -> member.email.contains(keyword);
            case NICKNAME -> member.nickname.contains(keyword);
            case ALL -> member.email.contains(keyword).or(member.nickname.contains(keyword));
        };
    }
}
