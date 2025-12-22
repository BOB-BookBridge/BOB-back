package com.bob.admin.application.port.out;

import java.util.UUID;

import com.bob.admin.application.port.result.activity.ManagementMemberPost;

public interface ManagementPostPort {

    ManagementMemberPost read(UUID writerId);
}
