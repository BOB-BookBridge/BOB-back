package com.bob.core.post.adapter.api;

import static com.bob.core.shared.web.symbol.ResponseSymbol.DELETED;
import static com.bob.core.shared.web.symbol.ResponseSymbol.OK;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;

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
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.post.adapter.api.request.ChangePostInfoRequest;
import com.bob.core.post.adapter.api.request.CreatePostRequest;
import com.bob.core.post.adapter.api.request.ReadPostsRequest;
import com.bob.core.post.adapter.api.response.CreatePostResponse;
import com.bob.core.post.adapter.api.response.PostDetailResponse;
import com.bob.core.post.adapter.api.response.PostsResponse;
import com.bob.core.post.adapter.api.response.internal.PostTrade;
import com.bob.core.post.application.dto.command.ChangePostInfoCommand;
import com.bob.core.post.application.dto.command.CreatePostCommand;
import com.bob.core.post.application.dto.command.RemovePostCommand;
import com.bob.core.post.application.dto.query.ReadPostDetailQuery;
import com.bob.core.post.application.dto.result.PostDetail;
import com.bob.core.post.application.dto.result.PostSummaries;
import com.bob.core.post.application.dto.result.PostSummary;
import com.bob.core.post.application.port.in.PostCreator;
import com.bob.core.post.application.port.in.PostModifier;
import com.bob.core.post.application.port.in.PostReader;
import com.bob.core.post.domain.repository.dsl.query.ReadPostsQuery;
import com.bob.core.shared.web.AuthenticationId;
import com.bob.core.shared.web.CommonResponse;
import com.bob.core.shared.web.symbol.ResponseSymbol;
import com.bob.core.trade.application.dto.query.ReadPostTradeQuery;
import com.bob.core.trade.application.dto.query.ReadTradeStatusMapQuery;
import com.bob.core.trade.application.port.in.TradeReader;
import com.bob.global.ratelimit.annotation.RateLimit;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostApi {

    private final PostCreator postCreator;
    private final PostReader postReader;
    private final PostModifier postModifier;

    private final TradeReader tradeReader;

    @RateLimit(
        name = "create-post",
        windowSecond = 60, maxRequest = 5,
        target = RateLimit.LimitTarget.MEMBER_ID,
        value = "#memberId"
    )
    @PostMapping
    public ResponseEntity<CreatePostResponse> createPost(
        @Valid @RequestBody CreatePostRequest request,
        @AuthenticationId UUID memberId
    ) {
        CreatePostCommand command = request.toCommand(memberId);

        CreatePostResponse response = CreatePostResponse.of(postCreator.create(command));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PostsResponse> readSummaries(
        ReadPostsRequest request, Pageable pageable,
        @AuthenticationId UUID memberId
    ) {
        ReadPostsQuery query = request.toQuery(memberId);

        PostSummaries result = postReader.readSummariesByQuery(query, pageable);

        List<Long> ids = result.posts().stream().map(PostSummary::id).toList();

        Map<Long, String> statusMap = new HashMap<>();

        if (memberId != null)
            statusMap.putAll(tradeReader.readTradeStatusMap(new ReadTradeStatusMapQuery(memberId, ids)));

        return ResponseEntity.ok(PostsResponse.from(result, statusMap));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> readDetail(@PathVariable Long postId, @AuthenticationId UUID memberId) {
        ReadPostDetailQuery query = new ReadPostDetailQuery(memberId, true);

        PostDetail detail = postReader.readDetail(postId, query);

        PostTrade postTrade = tradeReader.readByPostAndMember(new ReadPostTradeQuery(postId, memberId))
            .map(t -> new PostTrade(t.getId(), t.getStatus().name()))
            .orElse(null);

        return ResponseEntity.ok(PostDetailResponse.of(detail, postTrade));
    }

    @PatchMapping("/{postId}")
    public CommonResponse<ResponseSymbol> changeInfo(
        @PathVariable Long postId,
        @RequestBody ChangePostInfoRequest request,
        @AuthenticationId UUID memberId
    ) {
        ChangePostInfoCommand command = new ChangePostInfoCommand(
            memberId, request.bookStatus(), request.description(), request.wishOnly()
        );

        postModifier.changePostInfo(postId, command);

        return new CommonResponse<>(true, OK);
    }

    @DeleteMapping("/{postId}")
    public CommonResponse<ResponseSymbol> deletePost(@PathVariable Long postId, @AuthenticationId UUID memberId) {
        RemovePostCommand command = new RemovePostCommand(memberId);

        postModifier.deactivate(postId, command);

        return new CommonResponse<>(true, DELETED);
    }
}
