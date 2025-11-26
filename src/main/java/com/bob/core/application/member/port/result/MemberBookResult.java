package com.bob.core.application.member.port.result;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record MemberBookResult(
    Long id, String title, String author,
    Integer priceStandard, String cover, LocalDate pubDate
) {

}
