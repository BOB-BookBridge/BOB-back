package com.bob.core.application.post.port.in;

import com.bob.core.application.post.dto.command.RegisterPostFavoriteCommand;
import com.bob.core.application.post.dto.command.RemovePostFavoriteCommand;

public interface PostFavoriteManager {

    void registerFavorite(Long postId, RegisterPostFavoriteCommand command);

    void removeFavorite(Long postId, RemovePostFavoriteCommand command);
}
