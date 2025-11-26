package com.bob.core.adapter.post.api;

import static com.bob.core.adapter.common.symbol.ResponseSymbol.CREATED;
import static com.bob.core.adapter.common.symbol.ResponseSymbol.DELETED;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bob.core.adapter.common.AuthenticationId;
import com.bob.core.adapter.common.CommonResponse;
import com.bob.core.adapter.common.symbol.ResponseSymbol;
import com.bob.core.application.post.dto.command.RegisterPostFavoriteCommand;
import com.bob.core.application.post.dto.command.RemovePostFavoriteCommand;
import com.bob.core.application.post.dto.query.ReadPostFavoritesQuery;
import com.bob.core.application.post.dto.result.PostSummaries;
import com.bob.core.application.post.port.in.PostFavoriteManager;
import com.bob.core.application.post.port.in.PostReader;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostFavoriteApi {

    private final PostReader postReader;

    private final PostFavoriteManager favoriteManager;

    @PostMapping("/{postId}/favorite")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<ResponseSymbol> registerFavorite(@PathVariable Long postId, @AuthenticationId UUID memberId) {
        RegisterPostFavoriteCommand command = new RegisterPostFavoriteCommand(memberId);

        favoriteManager.registerFavorite(postId, command);

        return new CommonResponse<>(true, CREATED);
    }

    @GetMapping("/favorites")
    public ResponseEntity<PostSummaries> readFavorites(@AuthenticationId UUID memberId, Pageable pageable) {
        ReadPostFavoritesQuery query = new ReadPostFavoritesQuery(memberId);

        return ResponseEntity.ok(postReader.readFavoriteSummariesByQuery(query, pageable));
    }

    @DeleteMapping("/{postId}/favorite")
    public CommonResponse<ResponseSymbol> unregisterFavorite(
        @PathVariable Long postId,
        @AuthenticationId UUID memberId
    ) {
        RemovePostFavoriteCommand command = new RemovePostFavoriteCommand(memberId);

        favoriteManager.removeFavorite(postId, command);

        return new CommonResponse<>(true, DELETED);
    }
}
