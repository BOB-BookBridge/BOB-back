package com.bob.core.file.application.scheduler;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bob.core.file.application.port.in.FileRemover;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileCleanupScheduler {

    private final FileRemover fileRemover;

    @Scheduled(cron = "0 0 1 * * 1")
    public void removeUnusedFiles() {
        log.info("unused file remove scheduler started at {}", LocalDateTime.now());
        try {
            fileRemover.removeUnusedFiles();
            log.info("unused file removal completed successfully");
        } catch (Exception e) {
            log.error("unused file removal failed", e);
        }
    }
}
