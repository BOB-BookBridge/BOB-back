package com.bob.admin.application.port.out;

import java.util.UUID;

import com.bob.admin.application.port.result.activity.ManagementMemberTrade;

public interface ManagementTradePort {

    ManagementMemberTrade read(UUID traderId);
}
