package com.bob.core.inquiry.event;

import java.util.UUID;

public record InquiryProcessedEvent(Long inquiryId, UUID memberId) {

}
