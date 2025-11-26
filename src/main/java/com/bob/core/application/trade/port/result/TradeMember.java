package com.bob.core.application.trade.port.result;

import java.util.UUID;

public record TradeMember(UUID id, String nickname, String profileImageUrl) {

}
