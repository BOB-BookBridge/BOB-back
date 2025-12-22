package com.bob.core.book.application.dto.query;

public record ReadBooksQuery(String key, String keyword) {

    public static ReadBooksQuery of(String key, String keyword) {
        return new ReadBooksQuery(key, keyword);
    }
}
