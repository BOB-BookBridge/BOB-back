package com.bob.core.file.application.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

import java.util.List;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import com.bob.core.file.application.port.in.FileRemover;

@DisplayName("파일 정리 스케줄러 테스트")
@ExtendWith(MockitoExtension.class)
class FileCleanupSchedulerTest {

    @InjectMocks
    FileCleanupScheduler scheduler;

    @Mock
    FileRemover fileRemover;

    @Test
    void 파일_정리() {
        scheduler.removeUnusedFiles();

        then(fileRemover).should().removeUnusedFiles();
    }

    @Test
    void 파일_정리_중_예외_발생해도_정상_처리() {
        doThrow(new RuntimeException("S3 삭제 실패")).when(fileRemover).removeUnusedFiles();

        Logger logger = (Logger)LoggerFactory.getLogger(FileCleanupScheduler.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        scheduler.removeUnusedFiles();

        List<String> logs = listAppender.list.stream()
            .map(ILoggingEvent::getFormattedMessage)
            .toList();

        assertThat(logs).anyMatch(msg -> msg.contains("unused file removal failed"));
    }
}
