package com.bob.core.bookcase.application.port.in;

import static com.bob.support.fixture.bookcase.dto.command.BookcaseItemCommandFixture.createRegisterItemCommand;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.bookcase.application.dto.command.RegisterBookcaseItemCommand;
import com.bob.core.bookcase.domain.BookcaseItem;
import com.bob.support.annotation.ContainerTest;

@DisplayName("책장 등록 테스트")
@ContainerTest
record BookcaseRegisterTest(BookcaseRegister bookcaseRegister) {

    @Test
    void 책장_물품_등록() {
        RegisterBookcaseItemCommand command = createRegisterItemCommand();

        BookcaseItem result = bookcaseRegister.registerItem(command);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(MEMBER_ID);
    }
}
