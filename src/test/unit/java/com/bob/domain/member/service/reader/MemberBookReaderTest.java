package com.bob.domain.member.service.reader;

import static com.bob.support.fixture.domain.MemberBookFixture.DEFAULT_MEMBER_BOOK;
import static com.bob.support.fixture.domain.MemberBookFixture.DIFF_IN_TRADE_BOOK;
import static com.bob.support.fixture.domain.MemberBookFixture.NEW_MEMBER_BOOK;
import static com.bob.support.fixture.domain.MemberBookFixture.SAME_IN_TRADE_BOOK;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.member.entity.MemberBook;
import com.bob.domain.member.repository.MemberBookRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("회원 책 Reader 테스트")
@ExtendWith(MockitoExtension.class)
class MemberBookReaderTest {

  @InjectMocks
  private MemberBookReader reader;

  @Mock
  private MemberBookRepository repository;

  @Test
  void 회원_책_목록_조회() {
    // given
    UUID memberId = UUID.randomUUID();
    MemberBook mb1 = DEFAULT_MEMBER_BOOK();
    MemberBook mb2 = NEW_MEMBER_BOOK();
    List<MemberBook> expected = List.of(mb1, mb2);
    given(repository.findByMemberId(memberId)).willReturn(expected);

    // when
    List<MemberBook> actual = reader.readMemberBooksByMemberId(memberId);

    // then
    assertThat(actual).containsExactlyElementsOf(expected);
    then(repository).should().findByMemberId(memberId);
  }

  @Test
  void 회원_책_목록_조회_시_값이_없으면_빈_리스트_반환() {
    // given
    UUID memberId = UUID.randomUUID();
    given(repository.findByMemberId(memberId)).willReturn(List.of());

    // when
    List<MemberBook> actual = reader.readMemberBooksByMemberId(memberId);

    // then
    assertThat(actual).isEmpty();
    then(repository).should().findByMemberId(memberId);
  }

  @Test
  void 회원_책장_사용_가능_책_목록_조회() {
    // given
    UUID memberId = MEMBER_ID;
    List<Long> requires = List.of(-1L);
    given(repository.findAvailableByMemberId(memberId, requires)).willReturn(List.of(DEFAULT_MEMBER_BOOK(), NEW_MEMBER_BOOK()));

    // when
    List<MemberBook> result = reader.readAvailableMemberBooksByMemberId(memberId, requires);

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getUsageId()).isNull();
    assertThat(result.get(1).getUsageId()).isNull();
  }

  @Test
  void 회원_책장_사용_불가능_책_목록_조회() {
    // given
    UUID memberId = MEMBER_ID;
    List<Long> requires = List.of(-1L);
    given(repository.findUnavailableByMemberId(memberId, requires)).willReturn(List.of(DIFF_IN_TRADE_BOOK(), SAME_IN_TRADE_BOOK()));

    // when
    List<MemberBook> result = reader.readUnavailableMemberBooksByMemberId(memberId, requires);

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getUsageId()).isNotNull();
    assertThat(result.get(1).getUsageId()).isNotNull();
  }

  @Test
  void 회원_책_ID_목록_기반_목록_조회() {
    // given
    List<Long> bookIds = List.of(1L, 2L);
    MemberBook mb1 = DEFAULT_MEMBER_BOOK();
    MemberBook mb2 = NEW_MEMBER_BOOK();
    List<MemberBook> expected = List.of(mb1, mb2);
    given(repository.findAllByIdIn(bookIds)).willReturn(expected);

    // when
    List<MemberBook> actual = reader.readMemberBooksByBookIds(bookIds);

    // then
    assertThat(actual).containsExactlyElementsOf(expected);
    then(repository).should().findAllByIdIn(bookIds);
  }

  @Test
  void 회원_책_ID_기반_단건_조회() {
    // given
    Long id = 1L;
    MemberBook mb = DEFAULT_MEMBER_BOOK();
    given(repository.findById(id)).willReturn(Optional.of(mb));

    // when
    MemberBook actual = reader.readMemberBookById(id);

    // then
    assertThat(actual).isSameAs(mb);
    then(repository).should().findById(id);
  }

  @Test
  void 회원_책_ID_기반_단건_조회_시_존재하지_않으면_예외가_발생한다() {
    // given
    Long id = 99L;
    given(repository.findById(id)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> reader.readMemberBookById(id))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_EXIST_OBJECT.getMessage());
  }
}