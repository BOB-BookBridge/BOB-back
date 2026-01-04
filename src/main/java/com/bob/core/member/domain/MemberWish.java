package com.bob.core.member.domain;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.shared.entity.AbstractEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberWish extends AbstractEntity {

    private Long bookId;

    public static MemberWish createMemberWish(Long bookId) {
        return MemberWish.builder()
            .bookId(requireNonNull(bookId))
            .build();
    }
}
