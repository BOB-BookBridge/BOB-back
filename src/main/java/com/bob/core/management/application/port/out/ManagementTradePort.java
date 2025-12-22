package com.bob.core.management.application.port.out;

import java.util.UUID;

import com.bob.core.management.application.port.result.activity.ManagementMemberTrade;

public interface ManagementTradePort {

    ManagementMemberTrade read(UUID traderId);
}
