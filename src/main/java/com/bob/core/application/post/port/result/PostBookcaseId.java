package com.bob.core.application.post.port.result;

public record PostBookcaseId(Long id, Long bookId) {

    public static PostBookcaseId of(Long id, Long bookId) {
        return new PostBookcaseId(id, bookId);
    }
}
