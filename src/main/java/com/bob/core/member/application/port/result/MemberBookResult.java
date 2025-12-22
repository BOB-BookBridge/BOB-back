package com.bob.core.member.application.port.result;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record MemberBookResult(
    Long id, String title, String author,
    Integer priceStandard, String cover, LocalDate pubDate
) {

}
