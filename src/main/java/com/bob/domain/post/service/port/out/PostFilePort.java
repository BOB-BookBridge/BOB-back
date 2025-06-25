package com.bob.domain.post.service.port.out;

import com.bob.domain.post.service.dto.response.PostFileSummaryResponse;
import java.util.List;

public interface PostFilePort {

  PostFileSummaryResponse readPostFileSummaries(Long postId);

  void modifyReferenceId(List<String> fileNames, String domainId);
}
