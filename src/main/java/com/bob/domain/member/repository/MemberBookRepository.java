package com.bob.domain.member.repository;

import com.bob.domain.member.entity.MemberBook;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MemberBookRepository extends CrudRepository<MemberBook, Long> {

  @Query("""
      SELECT mb FROM MemberBook mb
      WHERE mb.memberId = :memberId
        AND mb.isRemove = false
      """)
  List<MemberBook> findByMemberId(UUID memberId);

  List<MemberBook> findAllByIdIn(List<Long> ids);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      UPDATE MemberBook m SET m.usageId = null
      WHERE m.usageId = :usageId
        AND m.isRemove = false
      """)
  void clearUsageId(@Param("usageId") Long usageId);
}
