package com.bob.domain.file.service.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.bob.domain.file.service.FileService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

@DisplayName("파일 정리 스케줄러 테스트")
@ExtendWith(MockitoExtension.class)
class FileCleanupSchedulerTest {

  @InjectMocks
  private FileCleanupScheduler scheduler;

  @Mock
  private FileService fileService;

  @Test
  @DisplayName("미참조 파일 삭제 서비스 메서드 호출 테스트")
  void 사용되지_않은_파일들을_성공적으로_삭제한다() {
    // when
    scheduler.removeUnusedFiles();

    // then
    verify(fileService, times(1)).removeUnusedFilesProcess();
  }

  @Test
  @DisplayName("파일 정리 중 예외가 발생해도 로그만 찍고 종료된다")
  void 파일_정리_중_예외가_발생해도_정상적으로_처리된다() {
    // given
    doThrow(new RuntimeException("S3 삭제 실패")).when(fileService).removeUnusedFilesProcess();
    Logger logger = (Logger) LoggerFactory.getLogger(FileCleanupScheduler.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    // when
    scheduler.removeUnusedFiles();

    // then
    verify(fileService).removeUnusedFilesProcess();
    List<String> logs = listAppender.list.stream()
        .map(ILoggingEvent::getFormattedMessage)
        .toList();
    assertThat(logs).anyMatch(msg -> msg.contains("unused file removal failed"));
  }
}
