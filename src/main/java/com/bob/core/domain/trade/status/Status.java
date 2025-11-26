package com.bob.core.domain.trade.status;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Status {
    CANCELED("취소"),
    REQUESTED("대기"), ACCEPTED("수락"), REJECTED("거절"), // 제안 단계
    RESERVED("예약"), COMPLETED("완료"); // 진행 단계

    private final String value;

    public static List<Status> convertFrom(List<String> values) {
        if (values == null || values.stream().anyMatch("ALL"::equalsIgnoreCase))
            return null;

        return values.stream()
            .map(s -> Status.valueOf(s.toUpperCase()))
            .distinct()
            .toList();
    }

    public String toPostStatusValue() {
        return switch (this) {
            case REQUESTED, ACCEPTED, REJECTED, CANCELED -> "READY";
            case RESERVED -> "RESERVED";
            case COMPLETED -> "COMPLETED";
        };
    }

    public String value() {
        return value;
    }
}
