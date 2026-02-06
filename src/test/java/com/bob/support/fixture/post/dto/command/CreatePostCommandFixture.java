package com.bob.support.fixture.post.dto.command;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.bob.core.post.application.dto.command.CreatePostCommand;

public class CreatePostCommandFixture {

    public static final List<String> FILE_NAMES = List.of(
        "post/uuid-1.png", "post/uuid-2.png", "post/uuid-3.png"
    );

    public static CreatePostCommand createPostCommand(UUID memberId, Integer categoryId, List<String> fileNames, String description) {
        return CreatePostCommand.builder()
            .memberId(memberId)
            .categoryId(categoryId)
            .bookStatus("BEST")
            .description(description)
            .bookIsbn("1020366172839")
            .bookTitle("JVM 밑바닥까지 파헤치기")
            .bookAuthor("저우즈밍")
            .bookDescription("핵심 JVM 원리 설명")
            .bookPriceStandard(43000)
            .bookCover("https://cover.url")
            .bookPubDate(LocalDate.of(2024, 4, 29))
            .fileNames(fileNames)
            .wishOnly(false)
            .build();
    }

    public static CreatePostCommand createPostCommand(UUID memberId) {
        return createPostCommand(memberId, 1, FILE_NAMES, "설명");
    }

    public static CreatePostCommand createPostCommand(String description) {
        return createPostCommand(MEMBER_ID, 1, FILE_NAMES, description);
    }

    public static CreatePostCommand createPostCommand() {
        return createPostCommand(MEMBER_ID);
    }
}
