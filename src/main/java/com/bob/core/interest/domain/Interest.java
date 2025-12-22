package com.bob.core.interest.domain;

import java.text.Normalizer;
import java.util.Objects;

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
public class Interest extends AbstractEntity {

    private String name;

    public static Interest createInterest(String name) {
        return Interest.builder()
            .name(normalize(name))
            .build();
    }

    /**
     * 관심사 이름 정규화
     * - null 및 빈 문자열 검증
     * - 앞뒤 공백 제거
     * - 유니코드 정규화 (NFC: 한글 자모 결합 정규화)
     * - 소문자 변환
     * - 연속된 공백을 하나의 공백으로 변환
     *
     * @param displayName 정규화할 관심사 이름
     * @return 정규화된 관심사 이름
     * @throws IllegalArgumentException displayName이 null이거나 빈 문자열인 경우
     */
    public static String normalize(String displayName) {
        Objects.requireNonNull(displayName, "관심사 이름은 null일 수 없습니다.");

        String normalized = displayName.trim();

        if (normalized.isEmpty())
            throw new IllegalArgumentException("관심사 이름은 빈 문자열일 수 없습니다.");

        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFC);

        return normalized.toLowerCase().replaceAll("\\s+", " ");
    }
}
