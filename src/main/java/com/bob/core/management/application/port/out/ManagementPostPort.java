package com.bob.core.management.application.port.out;

import java.util.UUID;

import com.bob.core.management.application.port.result.activity.ManagementMemberPost;

public interface ManagementPostPort {

    ManagementMemberPost read(UUID writerId);
}
