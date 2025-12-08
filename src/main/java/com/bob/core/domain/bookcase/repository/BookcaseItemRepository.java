package com.bob.core.domain.bookcase.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bob.core.domain.bookcase.BookcaseItem;

public interface BookcaseItemRepository extends CrudRepository<BookcaseItem, Long> {

    List<BookcaseItem> findAllByIdIn(List<Long> ids);

    @Query("""
        SELECT b FROM BookcaseItem b
         WHERE b.memberId = :memberId
           AND b.deletedAt IS NULL
        """)
    List<BookcaseItem> findByMemberId(UUID memberId);

    @Query("""
        SELECT b FROM BookcaseItem b
         WHERE b.memberId = :memberId
           AND b.deletedAt IS NULL
           AND (b.usageId IS NULL OR b.id IN :requires)
        """)
    List<BookcaseItem> findAvailableByMemberId(UUID memberId, List<Long> requires);

    @Query("""
        SELECT b FROM BookcaseItem b
         WHERE b.memberId = :memberId
           AND b.deletedAt IS NULL
           AND (b.usageId IS NOT NULL OR b.id IN :requires)
        """)
    List<BookcaseItem> findUnavailableByMemberId(UUID memberId, List<Long> requires);

    @Modifying
    @Query("""
        UPDATE BookcaseItem b SET b.usageId = null
         WHERE b.usageId = :usageId
           AND b.deletedAt IS NULL
        """)
    void freeUsageByUsageId(Long usageId);

    @Modifying
    @Query("""
        UPDATE BookcaseItem b SET b.usageId = null
         WHERE b.id IN :ids
           AND b.deletedAt IS NULL
        """)
    void freeUsageByIdIn(List<Long> ids);

    void removeAllByIdIn(List<Long> ids);
}
