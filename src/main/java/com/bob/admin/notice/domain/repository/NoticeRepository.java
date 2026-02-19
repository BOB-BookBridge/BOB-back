package com.bob.admin.notice.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bob.admin.notice.domain.Notice;
import com.bob.admin.notice.domain.NoticeType;

public interface NoticeRepository extends CrudRepository<Notice, Long> {

    @Query("""
        SELECT n
        FROM Notice n
        WHERE n.type = 'BANNER'
          AND (n.endsAt IS NULL OR n.endsAt > :now)
        ORDER BY n.id DESC
        """)
    List<Notice> findCurrentBanner(LocalDateTime now, Pageable pageable);

    List<Notice> findByTypeOrderByCreatedAtDesc(NoticeType type, Pageable pageable);
}
