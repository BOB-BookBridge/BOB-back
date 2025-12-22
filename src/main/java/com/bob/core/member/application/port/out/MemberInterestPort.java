package com.bob.core.member.application.port.out;

import java.util.List;

public interface MemberInterestPort {

    List<Long> registerAll(List<String> displayNames);
}
