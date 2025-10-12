package com.bob.domain.member.repository;

import com.bob.domain.member.entity.MemberBook;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface MemberBookRepository extends CrudRepository<MemberBook, Long> {

  @Query("""
      SELECT mb FROM MemberBook mb
      WHERE mb.memberId = :memberId
        AND mb.isRemove = false
      """)
  List<MemberBook> findByMemberId(UUID memberId);

  List<MemberBook> findAllByIdIn(List<Long> ids);
}
