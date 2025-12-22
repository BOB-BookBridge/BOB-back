package com.bob.core.member.domain.repository.dsl;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.repository.dsl.query.SearchMembersQuery;

public interface MemberQueryRepository {

    List<Member> findMembers(SearchMembersQuery query, Pageable pageable);

    Long countMembers(SearchMembersQuery query);
}
