package com.bob;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.boot.SpringApplication;

class BookBridgeApplicationTest {

    @Test
    void 애플리케이션_실행() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            BookBridgeApplication.main(new String[0]);

            mocked.verify(() -> SpringApplication.run(BookBridgeApplication.class, new String[0]));
        }
    }
}
