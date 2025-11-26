package com.bob.core.application.member.port.out;

import com.bob.core.application.member.port.result.MemberAreaResult;

public interface MemberAreaPort {

    MemberAreaResult read(Integer emdId);

    void authenticate(Integer emdId, Double lat, Double lon);
}
