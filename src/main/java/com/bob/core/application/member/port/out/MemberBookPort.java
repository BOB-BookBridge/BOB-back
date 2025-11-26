package com.bob.core.application.member.port.out;

import java.time.LocalDate;
import java.util.List;

import com.bob.core.application.member.port.result.MemberBookResult;

public interface MemberBookPort {

    Long register(
        String isbn, String title, String author,
        String description, Integer priceStandard, String cover, LocalDate pubDate
    );

    List<MemberBookResult> readAll(List<Long> bookIds);
}
