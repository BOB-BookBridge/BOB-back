package com.bob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class BookBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookBridgeApplication.class, args);
    }
}
