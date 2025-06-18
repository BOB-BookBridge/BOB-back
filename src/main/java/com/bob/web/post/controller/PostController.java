package com.bob.web.post.controller;

import static com.bob.web.common.symbol.ResponseSymbol.CREATED;
import static com.bob.web.common.symbol.ResponseSymbol.DELETED;
import static com.bob.web.common.symbol.ResponseSymbol.OK;
import static com.bob.web.post.request.ReadPostDetailRequest.toQuery;
import static com.bob.web.post.request.RegisterPostFavoriteRequest.toCommand;

import com.bob.domain.post.service.PostService;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.service.dto.response.PostsResponse;
import com.bob.web.common.AuthenticationId;
import com.bob.web.common.CommonResponse;
import com.bob.web.common.symbol.ResponseSymbol;
import com.bob.web.post.request.ChangePostRequest;
import com.bob.web.post.request.CreatePostRequest;
import com.bob.web.post.request.ReadFilteredPostsRequest;
import com.bob.web.post.request.ReadPostFavoritesRequest;
import com.bob.web.post.request.RemovePostRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CommonResponse<ResponseSymbol> handleCreatePost(
      @Valid @RequestBody CreatePostRequest request,
      @AuthenticationId UUID memberId
  ) {
    postService.createPostProcess(request.toCommand(memberId));
    return new CommonResponse<>(true, CREATED);
  }

  @PostMapping("/{postId}/favorite")
  @ResponseStatus(HttpStatus.CREATED)
  public CommonResponse<ResponseSymbol> handleRegisterFavoritePost(
      @PathVariable Long postId,
      @AuthenticationId UUID memberId
  ) {
    postService.registerPostFavoriteProcess(toCommand(memberId, postId));
    return new CommonResponse<>(true, CREATED);
  }

  @GetMapping
  public ResponseEntity<PostsResponse> handleReadFilteredPosts(
      ReadFilteredPostsRequest request,
      Pageable pageable
  ) {
    return ResponseEntity.ok(postService.readFilteredPostsProcess(request.toQuery(), pageable));
  }

  @GetMapping("/favorites")
  public ResponseEntity<PostsResponse> handleReadMemberFavoritePosts(
      @AuthenticationId UUID memberId,
      Pageable pageable
  ) {
    return ResponseEntity.ok(
        postService.readPostFavoritesProcess(ReadPostFavoritesRequest.toQuery(memberId), pageable));
  }

  @GetMapping("/{postId}")
  public ResponseEntity<PostDetailResponse> handleReadPostDetail(
      @PathVariable Long postId,
      @AuthenticationId UUID memberId
  ) {
    return ResponseEntity.ok(postService.readPostDetailProcess(toQuery(memberId, postId)));
  }

  @PatchMapping("/{postId}")
  public CommonResponse<ResponseSymbol> handleChangePost(
      @PathVariable Long postId,
      @RequestBody ChangePostRequest request,
      @AuthenticationId UUID memberId
  ) {
    postService.changePostProcess(request.toCommand(postId, memberId));
    return new CommonResponse<>(true, OK);
  }

  @DeleteMapping("/{postId}/favorite")
  public CommonResponse<ResponseSymbol> handleUnregisterFavoritePost(
      @PathVariable Long postId,
      @AuthenticationId UUID memberId
  ) {
    postService.unregisterPostFavoriteProcess(toCommand(memberId, postId));
    return new CommonResponse<>(true, DELETED);
  }

  @DeleteMapping("/{postId}")
  public CommonResponse<ResponseSymbol> handleRemovePost(
      @PathVariable Long postId,
      @AuthenticationId UUID memberId
  ) {
    postService.removePostProcess(RemovePostRequest.toCommand(memberId, postId));
    return new CommonResponse<>(true, DELETED);
  }
}
