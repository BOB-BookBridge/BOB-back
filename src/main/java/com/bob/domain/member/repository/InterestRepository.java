package com.bob.domain.member.repository;

import com.bob.domain.member.entity.Interest;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface InterestRepository extends CrudRepository<Interest, Long> {

  List<Interest> findByCanonicalNameIn(Collection<String> canonicalNames);

  // DB 변경 시 애플리케이션 try-catch 고려
  @Modifying
  @Query(value = """
        INSERT INTO interests (canonical_name)
        VALUES (:canonical)
        ON DUPLICATE KEY UPDATE id = id
      """, nativeQuery = true
  )
  int upsert(@Param("canonical") String canonical);
}
