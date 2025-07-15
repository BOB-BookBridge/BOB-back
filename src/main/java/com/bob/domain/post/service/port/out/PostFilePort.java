package com.bob.domain.post.service.port.out;

import com.bob.domain.file.service.dto.response.FilesResponse;
import java.util.List;

public interface PostFilePort {

  FilesResponse readPostFileSummaries(Long postId);

  void modifyReferenceId(List<String> fileNames, String domainId);
}
