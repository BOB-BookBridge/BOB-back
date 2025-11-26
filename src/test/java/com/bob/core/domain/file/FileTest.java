package com.bob.core.domain.file;

import static com.bob.core.domain.file.type.FileDomain.POST;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("파일 도메인 테스트")
class FileTest {

    @Test
    void 파일_생성() {
        File file = File.createFile(POST, "post/test.jpg", 0, MEMBER_ID);

        assertThat(file.getDomain()).isEqualTo(POST);
        assertThat(file.getName()).isEqualTo("post/test.jpg");
        assertThat(file.getSequence()).isEqualTo(0);
        assertThat(file.getUploader()).isEqualTo(MEMBER_ID);
        assertThat(file.getReferenceId()).isNull();
        assertThat(file.getCreatedAt()).isNotNull();
    }

    @Test
    void 참조_ID_매핑() {
        File file = File.createFile(POST, "post/test.jpg", 0, MEMBER_ID);

        String referenceId = "100";
        int newSequence = 5;

        file.mappingReferenceId(newSequence, referenceId);

        assertThat(file.getReferenceId()).isEqualTo(referenceId);
        assertThat(file.getSequence()).isEqualTo(newSequence);
    }
}
