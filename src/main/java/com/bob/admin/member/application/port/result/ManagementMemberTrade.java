package com.bob.admin.member.application.port.result;

import java.util.List;

public record ManagementMemberTrade(Long count, List<Long> sold, List<Long> bought) {

}
