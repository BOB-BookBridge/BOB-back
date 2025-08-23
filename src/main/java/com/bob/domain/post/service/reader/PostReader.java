package com.bob.domain.post.service.reader;

import com.bob.domain.post.entity.Post;
import com.bob.domain.post.repository.PostRepository;
import com.bob.domain.post.service.dto.query.ReadFilteredPostsQuery;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostReader {

  private final PostRepository postRepository;

  public List<Post> readFilteredPosts(ReadFilteredPostsQuery query, Pageable pageable) {
    return postRepository.findFilteredPosts(query, pageable);
  }

  public List<Post> readPostsByMember(UUID memberId) {
    return postRepository.findAllBySellerId(memberId);
  }

  public Post readPostById(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXIST_POST));
  }
}
