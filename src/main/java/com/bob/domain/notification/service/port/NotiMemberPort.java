package com.bob.domain.notification.service.port;

import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import java.util.UUID;

public interface NotiMemberPort {

  MemberProfileResponse readNotiMemberProfile(UUID memberId);
}
