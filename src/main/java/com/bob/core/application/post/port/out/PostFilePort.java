package com.bob.core.application.post.port.out;

import java.util.List;

import com.bob.core.application.post.port.result.PostFile;

public interface PostFilePort {

    List<PostFile> readPostFiles(Long postId);

    void changeReferenceId(List<String> fileNames, String referenceId);
}
