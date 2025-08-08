package com.bob.domain.file.service.scheduler;

import com.bob.domain.file.service.FileService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileCleanupScheduler {

  private final FileService fileService;

  @Scheduled(cron = "0 0 1 * * 1")
  public void removeUnusedFiles() {
    log.info("unused file remove scheduler started at {}", LocalDateTime.now());
    try {
      fileService.removeUnusedFilesProcess();
      log.info("unused file removal completed successfully");
    } catch (Exception e) {
      log.error("unused file removal failed", e);
    }
  }
}
