package com.bob.core.application.trade.dto.command;

import java.util.UUID;

public record ChangeTradeStatusCommand(UUID memberId, String status, String reason) {

}
