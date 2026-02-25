package com.bob.statistics.adapter.api.request;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PastOrPresent;

import org.springframework.format.annotation.DateTimeFormat;

import com.bob.shared.web.annotation.AfterDate;

public record ReadStatisticsTradeRequest(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @AfterDate("2025-05-01")
    LocalDate from,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @PastOrPresent(message = "미래 날짜는 허용되지 않습니다")
    LocalDate to
) {

    @AssertTrue(message = "from과 to는 함께 전달하거나 둘 다 비워야 합니다")
    public boolean hasBothDatesOrNone() {
        return (from == null && to == null) || (from != null && to != null);
    }

    @AssertTrue(message = "from은 to보다 늦을 수 없습니다")
    public boolean isValidRange() {
        if (from == null || to == null)
            return true;

        return !from.isAfter(to);
    }
}
