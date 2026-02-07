package com.bob.admin.post.application.port.out;

import java.util.List;

public interface ManagementPostReportPort {

    List<String> readReasonsByPostId(Long postId);
}
