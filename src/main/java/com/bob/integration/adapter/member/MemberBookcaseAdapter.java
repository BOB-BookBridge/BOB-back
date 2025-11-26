package com.bob.integration.adapter.member;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.bookcase.dto.query.ReadBookcaseQuery;
import com.bob.core.application.bookcase.dto.result.BookcaseItemDetail;
import com.bob.core.application.bookcase.port.in.BookcaseReader;
import com.bob.core.application.member.port.out.MemberBookcasePort;
import com.bob.core.application.member.port.result.MemberBookcaseResult;

@Component
@RequiredArgsConstructor
public class MemberBookcaseAdapter implements MemberBookcasePort {

    private final BookcaseReader bookcaseReader;

    @Override
    public List<MemberBookcaseResult> readAllDetail(UUID memberId) {
        ReadBookcaseQuery query = ReadBookcaseQuery.of(memberId);

        List<BookcaseItemDetail> result = bookcaseReader.readItemDetailsByQuery(query);

        return result.stream()
            .map(i -> new MemberBookcaseResult(i.id(), i.status(), i.title(), i.author(), i.cover(), i.available()))
            .toList();
    }
}
