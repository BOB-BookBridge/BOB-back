package com.bob.core.application.bookcase.port.in;

import static com.bob.global.exception.response.ApplicationError.BOOKCASE_ITEM_ACCESS_DENIED;
import static com.bob.global.exception.response.ApplicationError.BOOKCASE_ITEM_UNREMOVABLE;
import static com.bob.support.fixture.bookcase.domain.BookcaseItemFixture.createBookcaseItem;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.bookcase.dto.command.DeleteBookcaseItemCommand;
import com.bob.core.domain.bookcase.BookcaseItem;
import com.bob.core.domain.bookcase.repository.BookcaseItemRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("책장 삭제 테스트")
@ContainerTest
record BookcaseDeleterTest(BookcaseDeleter bookcaseDeleter, BookcaseItemRepository itemRepository, EntityManager em) {

    @Test
    void 책장_물품_삭제() {
        BookcaseItem item = itemRepository.save(createBookcaseItem());

        DeleteBookcaseItemCommand command = DeleteBookcaseItemCommand.of(MEMBER_ID);

        bookcaseDeleter.delete(item.getId(), command);

        em.flush();
        em.clear();

        BookcaseItem updated = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(updated.isDeleted()).isTrue();
    }

    @Test
    void 책장_물품_삭제_시_사용되는_곳이_있다면_사용자_예외가_발생한다() {
        BookcaseItem item = itemRepository.save(createBookcaseItem());
        item.updateUsageId(1L);

        DeleteBookcaseItemCommand command = DeleteBookcaseItemCommand.of(MEMBER_ID);

        assertThatThrownBy(() -> bookcaseDeleter.delete(item.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessageContaining(BOOKCASE_ITEM_UNREMOVABLE.getMessage().split("\\.")[0]);
    }

    @Test
    void 책장_물품_삭제_시_소유자가_아니면_사용자_예외가_발생한다() {
        BookcaseItem item = itemRepository.save(createBookcaseItem());

        DeleteBookcaseItemCommand command = DeleteBookcaseItemCommand.of(OTHER_MEMBER_ID);

        assertThatThrownBy(() -> bookcaseDeleter.delete(item.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(BOOKCASE_ITEM_ACCESS_DENIED.getMessage());
    }

    @Test
    void 책장_물품_목록_삭제() {
        BookcaseItem item1 = itemRepository.save(createBookcaseItem());
        BookcaseItem item2 = itemRepository.save(createBookcaseItem());

        List<Long> ids = List.of(item1.getId(), item2.getId());

        bookcaseDeleter.deleteItems(ids);

        em.flush();
        em.clear();

        BookcaseItem updated1 = itemRepository.findById(item1.getId()).orElseThrow();
        BookcaseItem updated2 = itemRepository.findById(item2.getId()).orElseThrow();
        assertThat(updated1.isDeleted()).isTrue();
        assertThat(updated2.isDeleted()).isTrue();
    }
}
