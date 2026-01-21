package com.bob.core.inquiry.application.port.result;

import java.util.UUID;

public record InquiryMember(UUID id, String nickname, String profileImageUrl) {

}
