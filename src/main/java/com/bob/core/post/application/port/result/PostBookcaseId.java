package com.bob.core.post.application.port.result;

public record PostBookcaseId(Long id, Long bookId) {

    public static PostBookcaseId of(Long id, Long bookId) {
        return new PostBookcaseId(id, bookId);
    }
}
