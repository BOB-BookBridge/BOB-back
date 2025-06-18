package com.bob.domain.post.service;

import static com.bob.domain.post.entity.PostFavorite.create;
import static com.bob.domain.post.service.helper.PostFavoriteHandler.safeRegister;
import static com.bob.global.exception.response.ApplicationError.ALREADY_POST_FAVORITE;

import com.bob.domain.post.entity.Post;
import com.bob.domain.post.entity.PostFavorite;
import com.bob.domain.post.repository.PostFavoriteRepository;
import com.bob.domain.post.service.dto.response.PostFavoritesResponse;
import com.bob.domain.post.service.reader.PostFavoriteReader;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostFavoriteService {

  private final PostFavoriteRepository postFavoriteRepository;
  private final PostFavoriteReader postFavoriteReader;

  @Transactional
  public void createPostFavoriteProcess(UUID memberId, Post post) {
    safeRegister(() -> postFavoriteRepository.save(create(memberId, post)), ALREADY_POST_FAVORITE);
  }

  @Transactional
  public void deletePostFavoriteProcess(UUID memberId, Long postId) {
    PostFavorite favorite = postFavoriteReader.readFavoritePostByMemberIdAndPostId(memberId, postId);
    postFavoriteRepository.delete(favorite);
  }

  @Transactional(readOnly = true)
  public boolean isFavorite(UUID memberId, Long postId) {
    return postFavoriteRepository.existsByMemberIdAndPostId(memberId, postId);
  }

  @Transactional(readOnly = true)
  public PostFavoritesResponse readMemberFavoritePosts(UUID memberId, Pageable pageable) {
    List<PostFavorite> postFavorites = postFavoriteReader.readFavoritePostsByMemberId(memberId, pageable);
    Long totalCount = postFavoriteRepository.countByMemberId(memberId);
    return PostFavoritesResponse.of(totalCount, postFavorites);
  }

  @Transactional
  public void removePostFavoriteProcess(Long postId) {
    postFavoriteRepository.deleteAllByPostId(postId);
  }
}
