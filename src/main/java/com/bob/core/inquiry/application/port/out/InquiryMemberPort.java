package com.bob.core.inquiry.application.port.out;

import java.util.UUID;

public interface InquiryMemberPort {

    String readNickname(UUID memberId);

    boolean isAuthorized(UUID memberId, String email);
}
