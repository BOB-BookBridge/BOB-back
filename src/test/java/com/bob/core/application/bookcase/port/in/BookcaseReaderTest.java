package com.bob.core.application.bookcase.port.in;

import static com.bob.core.application.bookcase.dto.query.SearchKey.AVAILABLE;
import static com.bob.core.application.bookcase.dto.query.SearchKey.UNAVAILABLE;
import static com.bob.global.exception.response.ApplicationError.NOT_EXIST_OBJECT;
import static com.bob.support.fixture.book.domain.BookFixture.DEFAULT_ISBN;
import static com.bob.support.fixture.bookcase.domain.BookcaseItemFixture.createBookcaseItem;
import static com.bob.support.fixture.bookcase.dto.command.BookcaseItemCommandFixture.createRegisterItemCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.bookcase.dto.query.ReadBookcaseQuery;
import com.bob.core.application.bookcase.dto.result.BookcaseItemDetail;
import com.bob.core.domain.bookcase.BookcaseItem;
import com.bob.core.domain.bookcase.repository.BookcaseItemRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("책장 조회 테스트")
@ContainerTest
record BookcaseReaderTest(
    BookcaseReader bookcaseReader, BookcaseRegister bookcaseRegister, BookcaseItemRepository itemRepository,
    EntityManager em
) {

    @Test
    void 책장_물품_조회() {
        BookcaseItem item = itemRepository.save(createBookcaseItem(1L));

        Long itemId = item.getId();

        em.flush();
        em.clear();

        BookcaseItem result = bookcaseReader.read(itemId);

        assertThat(result).isNotNull();
        assertThat(result.getBookId()).isEqualTo(1L);
    }

    @Test
    void 책장_물품_조회_시_존재하지_않으면_예외가_발생한다() {
        assertThatThrownBy(() -> bookcaseReader.read(99L))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(NOT_EXIST_OBJECT.getMessage());
    }

    @Test
    void ID_기반_책장_물품_조회() {
        BookcaseItem item1 = itemRepository.save(createBookcaseItem(1L));
        BookcaseItem item2 = itemRepository.save(createBookcaseItem(2L));

        List<Long> ids = List.of(item1.getId(), item2.getId());

        em.flush();
        em.clear();

        List<BookcaseItem> result = bookcaseReader.readItems(ids);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getBookId()).isEqualTo(1L);
        assertThat(result.get(1).getBookId()).isEqualTo(2L);
    }

    @Test
    void ID_기반_책장_물품_상세_조회() {
        BookcaseItem item1 = bookcaseRegister.registerItem(createRegisterItemCommand());
        BookcaseItem item2 = bookcaseRegister.registerItem(createRegisterItemCommand("0000000000000"));

        List<Long> ids = List.of(item1.getId(), item2.getId());

        em.flush();
        em.clear();

        List<BookcaseItemDetail> result = bookcaseReader.readItemDetails(ids);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).isbn()).isEqualTo(DEFAULT_ISBN);
        assertThat(result.get(1).isbn()).isEqualTo("0000000000000");
    }

    @Test
    void 쿼리_기반_책장_물품_상세_조회() {
        UUID memberId = UUID.randomUUID();

        BookcaseItem item1 = bookcaseRegister.registerItem(createRegisterItemCommand(memberId));
        item1.updateUsageId(1L);

        BookcaseItem item2 = bookcaseRegister.registerItem(createRegisterItemCommand(memberId, "0000000000000", "제목"));

        em.flush();
        em.clear();

        // 모든 상태의 책장 물품 조회
        ReadBookcaseQuery query = ReadBookcaseQuery.of(memberId);

        List<BookcaseItemDetail> result = bookcaseReader.readItemDetailsByQuery(query);

        assertThat(result).hasSize(2);

        // 사용 불가능 상태의 책장 물품 조회
        query = ReadBookcaseQuery.of(memberId, UNAVAILABLE.name(), null);

        result = bookcaseReader.readItemDetailsByQuery(query);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(item1.getId());

        // 사용 가능 상태의 책장 물품 조회
        query = ReadBookcaseQuery.of(memberId, AVAILABLE.name(), null);

        result = bookcaseReader.readItemDetailsByQuery(query);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(item2.getId());

        // 사용 가능 상태 + 필수 포함 책장 물품 조회
        query = ReadBookcaseQuery.of(memberId, AVAILABLE.name(), List.of(item1.getId()));

        result = bookcaseReader.readItemDetailsByQuery(query);

        assertThat(result).hasSize(2);
    }
}
