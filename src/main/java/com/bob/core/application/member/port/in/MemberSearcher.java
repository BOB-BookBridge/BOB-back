package com.bob.core.application.member.port.in;

import org.springframework.data.domain.Pageable;

import com.bob.core.application.member.dto.result.MemberSummaries;
import com.bob.core.domain.member.repository.dsl.query.SearchMembersQuery;

public interface MemberSearcher {

    MemberSummaries searchByQuery(SearchMembersQuery query, Pageable pageable);
}
