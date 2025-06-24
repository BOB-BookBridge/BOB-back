package com.bob.domain.post.service.port.out;

import com.bob.domain.post.service.dto.response.PostFileSummaryResponse;

public interface PostFilePort {

  PostFileSummaryResponse readPostFileSummaries(Long postId);

  void modifyReferenceId(String oldReferenceId, Long currentReferenceId);
}
