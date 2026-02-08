package com.bob.admin.post.application.port.out;

import java.util.List;
import java.util.UUID;

public interface ManagementPostReportPort {

    Integer register(UUID managerId, UUID reportedId, Long postId);

    List<String> readReasonsByPostId(Long postId);
}
