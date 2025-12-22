package com.bob.core.trade.application.port.result;

import java.util.UUID;

public record TradeMember(UUID id, String nickname, String profileImageUrl) {

}
