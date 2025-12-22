package com.bob.core.book.application.port.in;

import static com.bob.support.fixture.book.domain.BookFixture.createBook;
import static com.bob.support.fixture.book.dto.command.CreateBookCommandFixture.createRegisterBookCommand;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.book.application.dto.command.RegisterBookCommand;
import com.bob.core.book.domain.Book;
import com.bob.core.book.domain.repository.BookRepository;
import com.bob.support.annotation.ContainerTest;

@DisplayName("책 등록 테스트")
@ContainerTest
record BookRegisterTest(BookRegister bookRegister, BookRepository bookRepository, EntityManager em) {

    @Test
    void 책_등록() {
        RegisterBookCommand command = createRegisterBookCommand();

        Book result = bookRegister.register(command);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getIsbn()).isEqualTo(command.isbn());
    }

    @Test
    void 책_등록_시_이미_등록된_책이면_기존_책_반환() {
        Book existingBook = bookRepository.save(createBook());

        Long expectedBookId = existingBook.getId();

        RegisterBookCommand command = RegisterBookCommand.builder()
            .isbn(existingBook.getIsbn())
            .build();

        em.flush();
        em.clear();

        Book result = bookRegister.register(command);

        assertThat(result.getId()).isEqualTo(expectedBookId);
    }
}
