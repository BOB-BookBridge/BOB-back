package com.bob.admin.member.application.port.result;

import com.bob.admin.member.application.port.result.activity.ManagementMemberPost;
import com.bob.admin.member.application.port.result.activity.ManagementMemberTrade;

public record ManagementMemberActivity(ManagementMemberPost post, ManagementMemberTrade trade) {

}
