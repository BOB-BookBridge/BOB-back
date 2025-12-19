package com.bob.core.application.member.port.in;

import static com.bob.core.domain.member.Role.ADMIN;
import static com.bob.core.domain.member.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageRequest;

import com.bob.core.application.member.dto.result.MemberSummaries;
import com.bob.core.domain.member.Member;
import com.bob.core.domain.member.Role;
import com.bob.core.domain.member.repository.MemberRepository;
import com.bob.core.domain.member.repository.dsl.query.SearchKey;
import com.bob.core.domain.member.repository.dsl.query.SearchMembersQuery;
import com.bob.support.annotation.ContainerTest;

@DisplayName("회원 검색 테스트")
@ContainerTest
record MemberSearcherTest(MemberSearcher memberSearcher, MemberRepository memberRepository) {

    @Test
    void 쿼리_기반_회원_전체_검색() {
        long existMemberCounts = memberRepository.count();
        Role firstMemberRole = memberRepository.findAll().get(0).getRole();
        assertThat(firstMemberRole).isEqualTo(USER);

        var query = new SearchMembersQuery(SearchKey.ALL, null);
        var pageable = PageRequest.of(0, 20);

        MemberSummaries result = memberSearcher.searchByQuery(query, pageable);

        assertThat(result.totalCount()).isEqualTo(existMemberCounts);

        // ADMIN, USER 순 정렬
        Member firstSearchedMember = result.members().get(0);
        assertThat(firstSearchedMember.getRole()).isNotEqualTo(firstMemberRole);
        assertThat(firstSearchedMember.getRole()).isEqualTo(ADMIN);
    }

    @Test
    void 쿼리_기반_이메일_일치_회원_검색() {
        var query = new SearchMembersQuery(SearchKey.EMAIL, "test@test.com"); // 더미 회원 이메일
        var pageable = PageRequest.of(0, 20);

        MemberSummaries result = memberSearcher.searchByQuery(query, pageable);

        assertThat(result.totalCount()).isEqualTo(1);
        assertThat(result.members().get(0).getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void 쿼리_기반_닉네임_일치_회원_검색() {
        var query = new SearchMembersQuery(SearchKey.NICKNAME, "tester"); // 더미 회원 닉네임
        var pageable = PageRequest.of(0, 20);

        MemberSummaries result = memberSearcher.searchByQuery(query, pageable);

        assertThat(result.totalCount()).isEqualTo(1);
        assertThat(result.members().get(0).getNickname()).isEqualTo("tester");
    }
}
