package com.bob.core.application.post.port.out;

import java.util.UUID;

import com.bob.core.application.post.port.out.request.RegisterBookcaseRequest;
import com.bob.core.application.post.port.result.PostBookcaseId;

public interface PostBookcasePort {

    PostBookcaseId register(RegisterBookcaseRequest request);

    void allocate(UUID memberId, Long usageId, Long bookId);

    void free(Long usageId);
}
