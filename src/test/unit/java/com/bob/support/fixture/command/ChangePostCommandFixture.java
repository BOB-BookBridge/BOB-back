package com.bob.support.fixture.command;

import com.bob.domain.post.service.dto.command.ChangePostCommand;
import java.util.UUID;

public class ChangePostCommandFixture {

  public static ChangePostCommand DEFAULT_CHANGE_POST_COMMAND(UUID memberId, Long postId) {
    return new ChangePostCommand(
        postId,
        memberId,
        12000,
        "MEDIUM",
        "상태 좋음",
        false
    );
  }
}
