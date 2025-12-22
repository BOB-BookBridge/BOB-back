package com.bob.core.post.application.port.out;

import java.util.List;

import com.bob.core.post.application.port.result.PostFile;

public interface PostFilePort {

    List<PostFile> readPostFiles(Long postId);

    void changeReferenceId(List<String> fileNames, String referenceId);
}
