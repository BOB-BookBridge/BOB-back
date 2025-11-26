package com.bob.core.domain.post.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bob.core.domain.post.Post;
import com.bob.core.domain.post.repository.dsl.CustomPostRepository;

public interface PostRepository extends CrudRepository<Post, Long>, CustomPostRepository {

    List<Post> findAllByWriterId(UUID writerId);

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
