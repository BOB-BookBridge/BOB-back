package com.bob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Modulithic
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class BookBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookBridgeApplication.class, args);
    }
}
