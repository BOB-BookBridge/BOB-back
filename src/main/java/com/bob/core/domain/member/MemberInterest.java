package com.bob.core.domain.member;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.core.domain.AbstractEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberInterest extends AbstractEntity {

    private Long interestId;

    private String displayName;

    public static MemberInterest createMemberInterest(Long interestId, String displayName) {
        return MemberInterest.builder()
            .interestId(interestId)
            .displayName(displayName)
            .build();
    }
}
