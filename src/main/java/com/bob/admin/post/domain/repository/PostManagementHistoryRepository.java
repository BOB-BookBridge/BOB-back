package com.bob.admin.post.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.bob.admin.post.domain.PostManagementHistory;

public interface PostManagementHistoryRepository extends CrudRepository<PostManagementHistory, Long> {

    Optional<PostManagementHistory> findTopByPostIdOrderByProcessedAtDesc(Long postId);
}
