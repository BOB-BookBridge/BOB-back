package com.bob.admin.member.application.port.out;

import java.util.UUID;

import com.bob.admin.member.application.port.result.ManagementMemberTrade;

public interface ManagementMemberTradePort {

    ManagementMemberTrade read(UUID traderId);
}
