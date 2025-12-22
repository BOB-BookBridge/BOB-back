package com.bob.admin.member.application.port.out;

import java.util.UUID;

import com.bob.admin.member.application.port.result.activity.ManagementMemberTrade;

public interface ManagementMemberTradePort {

    ManagementMemberTrade read(UUID traderId);
}
