package com.bob.core.domain.member.repository.dsl;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.bob.core.domain.member.Member;
import com.bob.core.domain.member.repository.dsl.query.SearchMembersQuery;

public interface MemberQueryRepository {

    List<Member> findMembers(SearchMembersQuery query, Pageable pageable);

    Long countMembers(SearchMembersQuery query);
}
