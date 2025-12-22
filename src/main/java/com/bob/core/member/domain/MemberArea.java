package com.bob.core.member.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.core.shared.entity.AbstractEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberArea extends AbstractEntity {

    private Integer emdId;

    private LocalDate authenticatedAt;

    public static MemberArea createArea(Integer emdId) {
        return MemberArea.builder()
            .emdId(emdId)
            .authenticatedAt(LocalDate.now())
            .build();
    }

    public static MemberArea createNonAuthenticateArea(Integer emdId) {
        return MemberArea.builder()
            .emdId(emdId)
            .authenticatedAt(LocalDate.EPOCH)
            .build();
    }

    public void updateAuthentication(Integer emdId) {
        this.emdId = emdId;
        authenticatedAt = LocalDate.now();
    }

    public boolean isAuthenticated() {
        return !authenticatedAt.isBefore(LocalDate.now().minusMonths(1));
    }
}
