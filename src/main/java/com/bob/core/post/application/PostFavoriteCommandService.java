package com.bob.core.post.application;

import static com.bob.global.exception.response.ApplicationError.POST_FAVORITE_EXIST;
import static com.bob.global.exception.response.ApplicationError.POST_FAVORITE_NOT_EXIST;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.post.application.dto.command.RegisterPostFavoriteCommand;
import com.bob.core.post.application.dto.command.RemovePostFavoriteCommand;
import com.bob.core.post.application.port.in.PostFavoriteManager;
import com.bob.core.post.application.port.in.PostReader;
import com.bob.core.post.domain.Post;
import com.bob.core.post.domain.repository.PostRepository;
import com.bob.global.exception.exceptions.ApplicationException;

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
            throw new ApplicationException(POST_FAVORITE_EXIST);

        post.addFavorite(command.memberId());

        postRepository.increaseFavoriteCount(post.getId());
    }

    @Override
    public void removeFavorite(Long postId, RemovePostFavoriteCommand command) {
        Post post = postReader.read(postId);

        if (!post.isFavorite(command.memberId()))
            throw new ApplicationException(POST_FAVORITE_NOT_EXIST);

        post.removeFavorite(command.memberId());

        postRepository.decreaseFavoriteCount(postId);
    }
}
