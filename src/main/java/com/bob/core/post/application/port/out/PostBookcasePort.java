package com.bob.core.post.application.port.out;

import java.util.UUID;

import com.bob.core.post.application.port.out.request.RegisterBookcaseRequest;
import com.bob.core.post.application.port.result.PostBookcaseId;

public interface PostBookcasePort {

    PostBookcaseId register(RegisterBookcaseRequest request);

    void allocate(UUID memberId, Long usageId, Long bookId);

    void free(Long usageId);
}
