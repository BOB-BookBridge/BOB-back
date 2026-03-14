package com.bob.core.post.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.dsl.CustomPostRepository;
import com.bob.core.post.domain.repository.projection.PostAreaDistributionProjection;
import com.bob.core.post.domain.repository.projection.PostCategoryDistributionProjection;

public interface PostRepository extends CrudRepository<Post, Long>, CustomPostRepository {

    List<Post> findAllByWriterId(UUID writerId);

    @Query("""
        SELECT p.categoryId AS categoryId, COUNT(p) AS count
        FROM Post p
        WHERE p.createdAt >= :from AND p.createdAt < :toExclusive
        GROUP BY p.categoryId
        ORDER BY p.categoryId ASC
        """)
    List<PostCategoryDistributionProjection> findCategoryDistribution(LocalDateTime from, LocalDateTime toExclusive);

    @Query("""
        SELECT p.registrationAreaId AS emdId, COUNT(p) AS count
        FROM Post p
        WHERE p.createdAt >= :from AND p.createdAt < :toExclusive
        GROUP BY p.registrationAreaId
        ORDER BY p.registrationAreaId ASC
        """)
    List<PostAreaDistributionProjection> findAreaDistribution(LocalDateTime from, LocalDateTime toExclusive);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void increaseViewCount(Long id);

    @Modifying
    @Query("UPDATE Post p SET p.scrapCount = p.scrapCount + 1 WHERE p.id = :id")
    void increaseFavoriteCount(Long id);

    @Modifying
    @Query("UPDATE Post p SET p.scrapCount = p.scrapCount - 1 WHERE p.id = :id AND p.scrapCount > 0")
    void decreaseFavoriteCount(Long id);
}
