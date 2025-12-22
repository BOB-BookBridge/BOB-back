package com.bob.admin.member.application.port.out;

import java.util.UUID;

import com.bob.admin.member.application.port.result.activity.ManagementMemberPost;

public interface ManagementMemberPostPort {

    ManagementMemberPost read(UUID writerId);
}
