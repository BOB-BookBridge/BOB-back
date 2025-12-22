package com.bob.core.bookcase.adapter.api;

import static com.bob.support.fixture.bookcase.dto.command.BookcaseItemCommandFixture.createRegisterItemCommand;
import static com.bob.support.fixture.bookcase.dto.request.RegisterBookcaseRequestFixture.createRegisterBookcaseRequest;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.core.bookcase.adapter.api.request.RegisterBookcaseRequest;
import com.bob.core.bookcase.adapter.api.response.BookcaseItemDetailResponse;
import com.bob.core.bookcase.application.dto.command.RegisterBookcaseItemCommand;
import com.bob.core.bookcase.application.port.in.BookcaseRegister;
import com.bob.core.bookcase.domain.BookcaseItem;
import com.bob.core.bookcase.domain.repository.BookcaseItemRepository;
import com.bob.support.annotation.BobApiTest;
import com.bob.support.annotation.WithAuthMember;

@DisplayName("책장 API 테스트")
@BobApiTest
@RequiredArgsConstructor
class BookcaseApiTest {

    final MockMvcTester mvcTester;

    final BookcaseRegister bookcaseRegister;

    final BookcaseItemRepository bookcaseItemRepository;

    final ObjectMapper objectMapper;

    @Test
    @WithAuthMember
    void 책장_아이템_등록() throws JsonProcessingException {
        RegisterBookcaseRequest request = createRegisterBookcaseRequest("LOW");
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post()
            .uri("/members/bookcase")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result).hasStatus(201);

        List<BookcaseItem> items = bookcaseItemRepository.findByMemberId(MEMBER_ID);
        assertThat(items).isNotEmpty();

        BookcaseItem savedItem = items.stream()
            .filter(item -> !item.isDeleted())
            .filter(item -> item.getUsageId() == null)
            .findFirst()
            .orElseThrow();

        assertThat(savedItem.getMemberId()).isEqualTo(MEMBER_ID);
        assertThat(savedItem.getStatus().name()).isEqualTo("LOW");
    }

    @Test
    void 책장_조회() throws UnsupportedEncodingException, JsonProcessingException {
        MvcTestResult result = mvcTester.get()
            .uri("/members/{memberId}/bookcase", MEMBER_ID)
            .exchange();

        assertThat(result).hasStatusOk();

        List<BookcaseItemDetailResponse> response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<>() {
            }
        );

        assertThat(response).isNotEmpty();
        assertThat(response.get(0).title()).isEqualTo("JVM 밑바닥까지 파헤치기");
        assertThat(response.get(0).isbn()).isNotNull();
    }

    @Test
    void 책장_조회_필수_포함() throws UnsupportedEncodingException, JsonProcessingException {
        MvcTestResult result = mvcTester.get()
            .uri("/members/{memberId}/bookcase?require=1", MEMBER_ID)
            .exchange();

        assertThat(result).hasStatusOk();

        List<BookcaseItemDetailResponse> response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<>() {
            }
        );

        assertThat(response).isNotEmpty();
        assertThat(response.get(0).title()).isEqualTo("JVM 밑바닥까지 파헤치기");
        assertThat(response.get(0).isbn()).isNotNull();
    }

    @Test
    @WithAuthMember
    void 책장_아이템_삭제() {
        RegisterBookcaseItemCommand command = createRegisterItemCommand();

        BookcaseItem savedItem = bookcaseRegister.registerItem(command);
        Long itemId = savedItem.getId();

        MvcTestResult result = mvcTester.delete()
            .uri("/members/bookcase/{itemId}", itemId)
            .exchange();

        assertThat(result).hasStatusOk();

        BookcaseItem deletedItem = bookcaseItemRepository.findById(itemId).orElseThrow();
        assertThat(deletedItem.isDeleted()).isTrue();
    }
}
