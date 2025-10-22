package com.bob.domain.member.repository;

import com.bob.domain.member.entity.MemberBook;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface MemberBookRepository extends CrudRepository<MemberBook, Long> {

  List<MemberBook> findAllByIdIn(List<Long> ids);

  @Query("""
      SELECT mb FROM MemberBook mb
       WHERE mb.memberId = :memberId
         AND mb.isRemove = false
      """)
  List<MemberBook> findByMemberId(UUID memberId);

  @Query("""
      SELECT mb FROM MemberBook mb
       WHERE mb.memberId = :memberId
         AND mb.isRemove = false
         AND (mb.usageId IS NULL OR mb.id IN :requires)
      """)
  List<MemberBook> findAvailableByMemberId(UUID memberId, List<Long> requires);

  @Query("""
      SELECT mb FROM MemberBook mb
       WHERE mb.memberId = :memberId
         AND mb.isRemove = false
         AND (mb.usageId IS NOT NULL OR mb.id IN :requires)
      """)
  List<MemberBook> findUnavailableByMemberId(UUID memberId, List<Long> requires);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      UPDATE MemberBook m SET m.usageId = null
       WHERE m.usageId = :usageId
         AND m.isRemove = false
      """)
  void freeUsageByUsageId(Long usageId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      UPDATE MemberBook mb SET mb.usageId = null
       WHERE mb.id IN :ids
         AND mb.isRemove = false
      """)
  void freeUsageByIdIn(List<Long> ids);

  void removeAllByIdIn(List<Long> ids);
}
