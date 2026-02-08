package com.bob.admin.post.application.port.out;

import java.util.UUID;

public interface ManagementPostMemberPort {

    void ban(UUID reportedId);

    String readNickname(UUID memberId);
}
