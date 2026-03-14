package com.bob.core.post.application.port.in;

import java.util.List;

import com.bob.core.post.application.dto.command.ChangeMemberPostStatusCommand;
import com.bob.core.post.application.dto.command.ChangePostInfoCommand;
import com.bob.core.post.application.dto.command.ChangePostStatusCommand;
import com.bob.core.post.application.dto.command.ChangePostTradeProgressCommand;
import com.bob.core.post.application.dto.command.RemovePostCommand;
import com.bob.core.post.domain.Post;

public interface PostModifier {

    Post changePostInfo(Long postId, ChangePostInfoCommand command);

    Post changePostTradeProgress(Long postId, ChangePostTradeProgressCommand command);

    Post activate(Long postId);

    Post deactivate(Long postId, RemovePostCommand command);

    Post changeStatus(Long postId, ChangePostStatusCommand command);

    List<Post> changeStatusByAccountEvent(ChangeMemberPostStatusCommand command);
}
