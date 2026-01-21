package com.bob.core.inquiry.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.bob.core.inquiry.application.port.result.InquiryMember;

public interface InquiryMemberPort {

    InquiryMember read(UUID memberId);

    Optional<InquiryMember> findByEmail(String email);

    boolean isAuthorized(UUID memberId, String email);
}
