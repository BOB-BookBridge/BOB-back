package com.bob.core.member.application.port.in;

import org.springframework.data.domain.Pageable;

import com.bob.core.member.application.dto.result.MemberSummaries;
import com.bob.core.member.domain.repository.dsl.query.SearchMembersQuery;

public interface MemberSearcher {

    MemberSummaries searchByQuery(SearchMembersQuery query, Pageable pageable);
}
