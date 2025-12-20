package com.bob.core.application.management.port.out;

import java.util.UUID;

import com.bob.core.application.management.port.result.activity.ManagementMemberTrade;

public interface ManagementTradePort {

    ManagementMemberTrade read(UUID traderId);
}
