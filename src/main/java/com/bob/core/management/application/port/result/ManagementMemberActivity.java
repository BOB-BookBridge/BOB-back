package com.bob.core.management.application.port.result;

import com.bob.core.management.application.port.result.activity.ManagementMemberPost;
import com.bob.core.management.application.port.result.activity.ManagementMemberTrade;

public record ManagementMemberActivity(ManagementMemberPost post, ManagementMemberTrade trade) {

}
