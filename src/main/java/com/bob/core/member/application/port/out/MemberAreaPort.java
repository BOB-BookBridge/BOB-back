package com.bob.core.member.application.port.out;

import com.bob.core.member.application.port.result.MemberAreaResult;

public interface MemberAreaPort {

    MemberAreaResult read(Integer emdId);

    void authenticate(Integer emdId, Double lat, Double lon);
}
