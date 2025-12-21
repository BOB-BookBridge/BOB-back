package com.bob.core.domain.member.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bob.core.domain.member.Member;
import com.bob.core.domain.member.repository.dsl.MemberQueryRepository;

public interface MemberRepository extends JpaRepository<Member, UUID>, MemberQueryRepository {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);
}
