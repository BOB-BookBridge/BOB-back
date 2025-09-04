package com.bob.domain.member.repository;

import com.bob.domain.member.entity.MemberInterest;
import com.bob.domain.member.repository.projection.InterestEntry;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MemberInterestRepository extends CrudRepository<MemberInterest, Long> {

  @Query("""
      SELECT mi.displayName
      FROM MemberInterest mi
      WHERE mi.memberId = :memberId
      ORDER BY mi.id ASC
      """)
  List<String> findDisplayNamesByMemberId(@Param("memberId") UUID memberId);

  @Query("""
      SELECT new com.bob.domain.member.repository.projection.InterestEntry(i.id, i.canonicalName)
      FROM MemberInterest mi
        JOIN Interest i ON i.id = mi.interestId
      WHERE mi.memberId = :memberId
      """)
  List<InterestEntry> findCanonicalNameWithId(@Param("memberId") UUID memberId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      DELETE FROM MemberInterest mi
      WHERE mi.memberId = :memberId
        AND mi.interestId IN :interestIds
      """)
  void deleteByMemberIdAndInterestIds(@Param("memberId") UUID memberId, @Param("interestIds") Collection<Long> interestIds);

  /*@Query("""
      SELECT DISTINCT mi.memberId
      FROM MemberInterest mi
        JOIN Interest i ON i.id = mi.interestId
      WHERE i.canonicalName IN :canonicals
      """)
  List<UUID> findDistinctMemberIdsByCanonicals(@Param("canonicals") Collection<String> canonicals);*/
}
