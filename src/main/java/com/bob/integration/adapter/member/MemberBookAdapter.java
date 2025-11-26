package com.bob.integration.adapter.member;

import static com.bob.core.application.book.dto.command.RegisterBookCommand.of;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.book.dto.command.RegisterBookCommand;
import com.bob.core.application.book.port.in.BookReader;
import com.bob.core.application.book.port.in.BookRegister;
import com.bob.core.application.member.port.out.MemberBookPort;
import com.bob.core.application.member.port.result.MemberBookResult;
import com.bob.core.domain.book.Book;

@Component
@RequiredArgsConstructor
public class MemberBookAdapter implements MemberBookPort {

    private final BookRegister bookRegister;
    private final BookReader bookReader;

    @Override
    public Long register(
        String isbn, String title, String author,
        String description, Integer priceStandard, String cover, LocalDate pubDate
    ) {
        RegisterBookCommand command = of(isbn, title, author, description, priceStandard, cover, pubDate);

        Book book = bookRegister.register(command);

        return book.getId();
    }

    @Override
    public List<MemberBookResult> readAll(List<Long> bookIds) {
        List<Book> books = bookReader.readAll(bookIds);

        return books.stream().map(this::convert).toList();
    }

    private MemberBookResult convert(Book book) {
        return MemberBookResult.builder()
            .id(book.getId())
            .title(book.getTitle())
            .author(book.getAuthor())
            .priceStandard(book.getPriceStandard())
            .cover(book.getCover())
            .pubDate(book.getPubDate())
            .build();
    }
}
