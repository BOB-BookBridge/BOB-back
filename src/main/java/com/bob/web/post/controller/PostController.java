package com.bob.web.post.controller;

import static com.bob.web.common.symbol.ResponseSymbol.CREATED;
import static com.bob.web.common.symbol.ResponseSymbol.DELETED;
import static com.bob.web.common.symbol.ResponseSymbol.OK;
import static com.bob.web.post.request.ReadPostDetailRequest.toQuery;
import static com.bob.web.post.request.RegisterPostFavoriteRequest.toCommand;

import com.bob.domain.post.service.dto.response.PostCreateResponse;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.service.dto.response.PostsResponse;
import com.bob.domain.post.usecase.PostDeleteUseCase;
import com.bob.domain.post.usecase.PostModifyUseCase;
import com.bob.domain.post.usecase.PostReadUseCase;
import com.bob.domain.post.usecase.PostWriteUseCase;
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

  private final PostWriteUseCase writeUseCase;
  private final PostReadUseCase readUseCase;
  private final PostModifyUseCase modifyUseCase;
  private final PostDeleteUseCase deleteUseCase;

  @PostMapping
  public ResponseEntity<PostCreateResponse> handleCreatePost(
      @Valid @RequestBody CreatePostRequest request,
      @AuthenticationId UUID memberId
  ) {
    PostCreateResponse response = writeUseCase.createPostProcess(request.toCommand(memberId));
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/{postId}/favorite")
  @ResponseStatus(HttpStatus.CREATED)
  public CommonResponse<ResponseSymbol> handleRegisterFavoritePost(
      @PathVariable Long postId,
      @AuthenticationId UUID memberId
  ) {
    writeUseCase.registerPostFavoriteProcess(toCommand(memberId, postId));
    return new CommonResponse<>(true, CREATED);
  }

  @GetMapping
  public ResponseEntity<PostsResponse> handleReadFilteredPosts(
      ReadFilteredPostsRequest request,
      Pageable pageable
  ) {
    return ResponseEntity.ok(readUseCase.readFilteredPostsProcess(request.toQuery(), pageable));
  }

  @GetMapping("/favorites")
  public ResponseEntity<PostsResponse> handleReadMemberFavoritePosts(
      @AuthenticationId UUID memberId,
      Pageable pageable
  ) {
    return ResponseEntity.ok(
        readUseCase.readPostFavoritesProcess(ReadPostFavoritesRequest.toQuery(memberId), pageable));
  }

  @GetMapping("/{postId}")
  public ResponseEntity<PostDetailResponse> handleReadPostDetail(
      @PathVariable Long postId,
      @AuthenticationId UUID memberId
  ) {
    return ResponseEntity.ok(readUseCase.readPostDetailProcess(toQuery(memberId, postId)));
  }

  @PatchMapping("/{postId}")
  public CommonResponse<ResponseSymbol> handleChangePost(
      @PathVariable Long postId,
      @RequestBody ChangePostRequest request,
      @AuthenticationId UUID memberId
  ) {
    modifyUseCase.changePostProcess(request.toCommand(postId, memberId));
    return new CommonResponse<>(true, OK);
  }

  @DeleteMapping("/{postId}/favorite")
  public CommonResponse<ResponseSymbol> handleUnregisterFavoritePost(
      @PathVariable Long postId,
      @AuthenticationId UUID memberId
  ) {
    deleteUseCase.unregisterPostFavoriteProcess(toCommand(memberId, postId));
    return new CommonResponse<>(true, DELETED);
  }

  @DeleteMapping("/{postId}")
  public CommonResponse<ResponseSymbol> handleRemovePost(
      @PathVariable Long postId,
      @AuthenticationId UUID memberId
  ) {
    deleteUseCase.removePostProcess(RemovePostRequest.toCommand(memberId, postId));
    return new CommonResponse<>(true, DELETED);
  }
}
