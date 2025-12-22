package com.bob.core.bookcase.application;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.bookcase.application.dto.query.ReadBookcaseQuery;
import com.bob.core.bookcase.application.dto.result.BookcaseItemDetail;
import com.bob.core.bookcase.application.port.in.BookcaseReader;
import com.bob.core.bookcase.application.port.out.BookcaseItemBookPort;
import com.bob.core.bookcase.domain.BookcaseItem;
import com.bob.core.bookcase.domain.repository.BookcaseItemRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookCaseQueryService implements BookcaseReader {

    private final BookcaseItemRepository itemRepository;

    private final BookcaseItemBookPort bookPort;

    @Override
    public BookcaseItem read(Long id) {
        return itemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("책장 도서를 찾을 수 없습니다. id : " + id));
    }

    @Override
    public List<BookcaseItem> readItems(List<Long> ids) {
        return itemRepository.findAllByIdIn(ids);
    }

    @Override
    public List<BookcaseItemDetail> readItemDetails(List<Long> ids) {
        List<BookcaseItem> bookcases = readItems(ids);
        return getBookcaseDetail(bookcases);
    }

    @Override
    public List<BookcaseItemDetail> readItemDetailsByQuery(ReadBookcaseQuery query) {
        List<BookcaseItem> bookcases = switch (query.key()) {
            case ALL -> readItemsByQuery(query);
            case AVAILABLE -> readAllAvailableItems(query);
            case UNAVAILABLE -> readAllUnavailableItems(query);
        };
        return getBookcaseDetail(bookcases);
    }

    private List<BookcaseItem> readItemsByQuery(ReadBookcaseQuery query) {
        return itemRepository.findByMemberId(query.memberId());
    }

    private List<BookcaseItem> readAllAvailableItems(ReadBookcaseQuery query) {
        return itemRepository.findAvailableByMemberId(query.memberId(), query.requires());
    }

    private List<BookcaseItem> readAllUnavailableItems(ReadBookcaseQuery query) {
        return itemRepository.findUnavailableByMemberId(query.memberId(), query.requires());
    }

    private List<BookcaseItemDetail> getBookcaseDetail(List<BookcaseItem> bookcases) {
        List<Long> bookIds = bookcases.stream().map(BookcaseItem::getBookId).toList();
        return BookcaseItemDetail.listFrom(bookcases, bookPort.readBooks(bookIds));
    }
}
