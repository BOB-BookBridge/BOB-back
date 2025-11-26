package com.bob.core.domain.chat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bob.core.domain.chat.Chatroom;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    @Query("""
          SELECT cr
          FROM Chatroom cr
            JOIN cr.members crm
          WHERE crm.memberId = :memberId
            AND crm.exitedAt IS NULL
        """)
    List<Chatroom> findAllByMemberId(UUID memberId);

    @Query("""
          SELECT cr
          FROM Chatroom cr
            JOIN cr.members crm
          WHERE cr.postId = :postId
            AND crm.memberId = :buyerId
        """)
    Optional<Chatroom> findByPostAndBuyer(Long postId, UUID buyerId);
}
