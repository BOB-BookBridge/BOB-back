package com.bob.support.fixture.post.dto.request;

public class CreatePostRequestFixture {

    public static String createCreatePostRequest() {
        return """
            {
                "categoryId": 1,
                "bookStatus": "BEST",
                "description": "깨끗한 상태",
                "book": {
                    "isbn": "9788966264414",
                    "title": "JVM 밑바닥까지 파헤치기",
                    "author": "저우즈밍",
                    "description": "JVM 핵심 원리",
                    "priceStandard": 43000,
                    "cover": "https://cover.jpg",
                    "pubDate": "2024-04-29"
                },
                "fileNames": ["image1.jpg", "image2.jpg"],
                "wishOnly": false
            }
            """;
    }
}
