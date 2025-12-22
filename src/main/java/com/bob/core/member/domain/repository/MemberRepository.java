package com.bob.core.member.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.repository.dsl.MemberQueryRepository;

public interface MemberRepository extends JpaRepository<Member, UUID>, MemberQueryRepository {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);
}
