package com.bob.admin.notice.application.port.out;

import java.util.UUID;

public interface NoticeMemberPort {

    String readNickname(UUID memberId);
}
