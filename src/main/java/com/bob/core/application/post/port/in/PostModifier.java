package com.bob.core.application.post.port.in;

import com.bob.core.application.post.dto.command.ChangeMemberPostStatusCommand;
import com.bob.core.application.post.dto.command.ChangePostInfoCommand;
import com.bob.core.application.post.dto.command.ChangePostTradeProgressCommand;
import com.bob.core.application.post.dto.command.RemovePostCommand;
import com.bob.core.domain.post.Post;

public interface PostModifier {

    Post changePostInfo(Long postId, ChangePostInfoCommand command);

    Post changePostTradeProgress(Long postId, ChangePostTradeProgressCommand command);

    Post activate(Long postId);

    Post deactivate(Long postId, RemovePostCommand command);

    void changeStatusByAccountEvent(ChangeMemberPostStatusCommand command);
}
