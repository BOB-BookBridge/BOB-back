package com.bob.core.application.post;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.post.dto.command.RegisterPostFavoriteCommand;
import com.bob.core.application.post.dto.command.RemovePostFavoriteCommand;
import com.bob.core.application.post.port.in.PostFavoriteManager;
import com.bob.core.application.post.port.in.PostReader;
import com.bob.core.domain.post.Post;
import com.bob.core.domain.post.repository.PostRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;

@Service
@Transactional
@RequiredArgsConstructor
public class PostFavoriteCommandService implements PostFavoriteManager {

    private final PostRepository postRepository;

    private final PostReader postReader;

    @Override
    public void registerFavorite(Long postId, RegisterPostFavoriteCommand command) {
        Post post = postReader.read(postId);

        if (post.isFavorite(command.memberId()))
            throw new ApplicationException(ApplicationError.ALREADY_POST_FAVORITE);

        post.addFavorite(command.memberId());

        postRepository.increaseFavoriteCount(post.getId());
    }

    @Override
    public void removeFavorite(Long postId, RemovePostFavoriteCommand command) {
        Post post = postReader.read(postId);

        if (!post.isFavorite(command.memberId()))
            throw new ApplicationException(ApplicationError.INVALID_POST_FAVORITE);

        post.removeFavorite(command.memberId());

        postRepository.decreaseFavoriteCount(postId);
    }
}
