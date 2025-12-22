package com.bob.core.post.application.port.result;

public record PostMemberWishResult(String title, String author, String cover) {

    public static PostMemberWishResult of(String title, String author, String cover) {
        return new PostMemberWishResult(title, author, cover);
    }
}
