package com.bob.support.fixture.file.domain;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;

import java.util.UUID;

import org.springframework.test.util.ReflectionTestUtils;

import com.bob.core.file.domain.File;
import com.bob.core.file.domain.type.FileDomain;

public class FileFixture {

    public static File createFile(FileDomain domain, String name, int sequence, String refId, UUID memberId) {
        File file = File.createFile(domain, name, sequence, memberId);
        ReflectionTestUtils.setField(file, "referenceId", refId);
        return file;
    }

    public static File createFile(String name, int sequence, String refId, UUID memberId) {
        return createFile(FileDomain.POST, name, sequence, refId, memberId);
    }

    public static File createFile(String name, int sequence, String refId) {
        return createFile(name, sequence, refId, MEMBER_ID);
    }

    public static File createFile() {
        return createFile("post/test.jpg", 0, null);
    }
}
