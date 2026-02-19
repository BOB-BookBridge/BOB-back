package com.bob.core.member.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.repository.dsl.MemberQueryRepository;

public interface MemberRepository extends JpaRepository<Member, UUID>, MemberQueryRepository {

    Optional<Member> findByEmail(String email);

    @Query("""
        SELECT m.id
        FROM Member m
        WHERE m.status = 'ACTIVE'
        """)
    List<UUID> findAllActiveMemberIds();

    boolean existsByEmail(String email);
}
