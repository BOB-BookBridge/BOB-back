package com.bob.core.application.management.port.out;

import java.util.UUID;

import com.bob.core.application.management.port.result.activity.ManagementMemberPost;

public interface ManagementPostPort {

    ManagementMemberPost read(UUID writerId);
}
