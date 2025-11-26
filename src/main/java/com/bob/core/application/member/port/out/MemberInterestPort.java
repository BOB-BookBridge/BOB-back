package com.bob.core.application.member.port.out;

import java.util.List;

public interface MemberInterestPort {

    List<Long> registerAll(List<String> displayNames);
}
