package com.bob.core.inquiry.application.dto.command;

import java.util.UUID;

import com.bob.core.inquiry.domain.InquiryStatus;

public record ChangeInquiryStatusCommand(UUID managerId, InquiryStatus status, String reply) {

}
