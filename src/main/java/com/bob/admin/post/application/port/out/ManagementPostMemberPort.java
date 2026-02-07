package com.bob.admin.post.application.port.out;

import java.util.UUID;

public interface ManagementPostMemberPort {

    String readNickname(UUID memberId);
}
