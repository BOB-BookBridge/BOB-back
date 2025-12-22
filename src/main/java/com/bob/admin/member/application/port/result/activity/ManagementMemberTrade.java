package com.bob.admin.member.application.port.result.activity;

import java.util.List;

public record ManagementMemberTrade(Long count, List<Long> sold, List<Long> bought) {

}
