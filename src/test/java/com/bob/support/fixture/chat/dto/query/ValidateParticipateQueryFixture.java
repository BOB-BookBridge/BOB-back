package com.bob.support.fixture.chat.dto.query;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;

import java.util.UUID;

import com.bob.core.application.chat.dto.query.ValidateParticipantQuery;

public class ValidateParticipateQueryFixture {

    public static ValidateParticipantQuery validateParticipantQuery(UUID memberId) {
        return new ValidateParticipantQuery(memberId);
    }

    public static ValidateParticipantQuery validateParticipantQuery() {
        return validateParticipantQuery(MEMBER_ID);
    }
}
