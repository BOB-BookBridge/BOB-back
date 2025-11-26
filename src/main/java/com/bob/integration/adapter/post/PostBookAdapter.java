package com.bob.integration.adapter.post;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.book.dto.query.ReadBooksQuery;
import com.bob.core.application.book.port.in.BookReader;
import com.bob.core.application.post.port.out.PostBookPort;
import com.bob.core.application.post.port.result.PostBook;
import com.bob.core.domain.book.Book;

@Component
@RequiredArgsConstructor
public class PostBookAdapter implements PostBookPort {

    private final BookReader bookReader;

    @Override
    public PostBook read(Long id) {
        Book book = bookReader.read(id);

        return PostBook.builder()
            .id(book.getId())
            .isbn(book.getIsbn())
            .title(book.getTitle())
            .author(book.getAuthor())
            .description(book.getDescription())
            .pubDate(book.getPubDate())
            .build();
    }

    @Override
    public List<Long> searchAllIds(String key, String keyword) {
        return bookReader.readAllIdsByQuery(ReadBooksQuery.of(key, keyword));
    }
}
