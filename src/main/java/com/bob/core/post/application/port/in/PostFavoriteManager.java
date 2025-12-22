package com.bob.core.post.application.port.in;

import com.bob.core.post.application.dto.command.RegisterPostFavoriteCommand;
import com.bob.core.post.application.dto.command.RemovePostFavoriteCommand;

public interface PostFavoriteManager {

    void registerFavorite(Long postId, RegisterPostFavoriteCommand command);

    void removeFavorite(Long postId, RemovePostFavoriteCommand command);
}
