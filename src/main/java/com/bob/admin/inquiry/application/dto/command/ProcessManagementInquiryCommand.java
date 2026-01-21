package com.bob.admin.inquiry.application.dto.command;

import java.util.UUID;

public record ProcessManagementInquiryCommand(UUID managerId, String status, String reply) {

}
