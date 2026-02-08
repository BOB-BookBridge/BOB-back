package com.bob.admin.post.application.port.in;

import com.bob.admin.post.application.dto.command.ProcessManagementPostStatusCommand;

public interface ManagementPostProcessor {

    void process(Long postId, ProcessManagementPostStatusCommand command);
}
