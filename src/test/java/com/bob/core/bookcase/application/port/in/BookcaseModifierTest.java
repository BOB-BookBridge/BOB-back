package com.bob.core.bookcase.application.port.in;

import static com.bob.global.exception.response.ApplicationError.BOOKCASE_ITEM_ACCESS_DENIED;
import static com.bob.global.exception.response.ApplicationError.BOOKCASE_ITEM_ALREADY_USE;
import static com.bob.global.exception.response.ApplicationError.BOOKCASE_ITEM_UNAVAILABLE;
import static com.bob.support.fixture.bookcase.domain.BookcaseItemFixture.createBookcaseItem;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.bookcase.application.dto.command.AllocateUsageCommand;
import com.bob.core.bookcase.application.dto.command.FreeUsageByRefIdCommand;
import com.bob.core.bookcase.application.dto.command.FreeUsageCommand;
import com.bob.core.bookcase.domain.BookcaseItem;
import com.bob.core.bookcase.domain.repository.BookcaseItemRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("책장 수정 테스트")
@ContainerTest
record BookcaseModifierTest(
    BookcaseModifier bookcaseModifier, BookcaseItemRepository itemRepository,
    EntityManager em
) {

    @Test
    void 책장_물품_사용처_할당() {
        BookcaseItem item = itemRepository.save(createBookcaseItem());
        assertThat(item.getUsageId()).isNull();

        AllocateUsageCommand command = AllocateUsageCommand.of(1L);

        bookcaseModifier.allocate(List.of(item.getId()), command);

        em.flush();
        em.clear();

        BookcaseItem updated = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(updated.getUsageId()).isNotNull();
    }

    @Test
    void 책장_물품_사용처_할당_시_본인_소유가_아니면_사용자_예외가_발생한다() {
        BookcaseItem item = itemRepository.save(createBookcaseItem());
        assertThat(item.getUsageId()).isNull();

        AllocateUsageCommand command = AllocateUsageCommand.of(OTHER_MEMBER_ID, 1L);

        assertThatThrownBy(() -> bookcaseModifier.allocate(List.of(item.getId()), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(BOOKCASE_ITEM_ACCESS_DENIED.getMessage());
    }

    @Test
    void 책장_물품_사용처_할당_시_물품이_삭제_되었으면_사용자_예외가_발생한다() {
        BookcaseItem item = itemRepository.save(createBookcaseItem());
        item.delete();
        assertThat(item.getDeletedAt()).isNotNull();

        AllocateUsageCommand exCommand = new AllocateUsageCommand(MEMBER_ID, 1L);

        assertThatThrownBy(() -> bookcaseModifier.allocate(List.of(item.getId()), exCommand))
            .isInstanceOf(ApplicationException.class)
            .hasMessageContaining(BOOKCASE_ITEM_UNAVAILABLE.getMessage().split("\\.")[0]);
    }

    @Test
    void 책장_물품_사용처_할당_시_이미_사용중이라면_사용자_예외가_발생한다() {
        BookcaseItem item = itemRepository.save(createBookcaseItem());
        item.updateUsageId(1L);
        assertThat(item.getUsageId()).isNotNull();

        AllocateUsageCommand exCommand = new AllocateUsageCommand(MEMBER_ID, 2L);

        assertThatThrownBy(() -> bookcaseModifier.allocate(List.of(item.getId()), exCommand))
            .isInstanceOf(ApplicationException.class)
            .hasMessageContaining(BOOKCASE_ITEM_ALREADY_USE.getMessage().split("\\.")[0]);

        // 같은 사용처인 경우 예외가 발생하지 않는다.
        AllocateUsageCommand command = new AllocateUsageCommand(MEMBER_ID, 1L);

        assertDoesNotThrow(() -> bookcaseModifier.allocate(List.of(item.getId()), command));
    }

    @Test
    void 책장_물품_사용처_해제() {
        BookcaseItem item = itemRepository.save(createBookcaseItem());
        item.updateUsageId(1L);
        assertThat(item.getUsageId()).isNotNull();

        FreeUsageCommand command = FreeUsageCommand.of(MEMBER_ID);

        bookcaseModifier.free(List.of(item.getId()), command);

        em.flush();
        em.clear();

        BookcaseItem updated = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(updated.getUsageId()).isNull();
    }

    @Test
    void 책장_물품_사용처_해제_시_본인_소유가_아니면_사용자_예외가_발생한다() {
        BookcaseItem item = itemRepository.save(createBookcaseItem());
        item.updateUsageId(1L);

        FreeUsageCommand command = FreeUsageCommand.of(OTHER_MEMBER_ID);

        assertThatThrownBy(() -> bookcaseModifier.free(List.of(item.getId()), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(BOOKCASE_ITEM_ACCESS_DENIED.getMessage());
    }

    @Test
    void 사용처_ID_기반_책장_물품_사용처_해제() {
        Long usageId = 1L;

        BookcaseItem item = itemRepository.save(createBookcaseItem());
        item.updateUsageId(usageId);
        assertThat(item.getUsageId()).isNotNull();

        FreeUsageByRefIdCommand command = FreeUsageByRefIdCommand.of(usageId);

        bookcaseModifier.freeByRefId(command);

        em.flush();
        em.clear();

        BookcaseItem updated = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(updated.getUsageId()).isNull();
    }
}
