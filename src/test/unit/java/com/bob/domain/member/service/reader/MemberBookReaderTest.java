package com.bob.domain.member.service.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.member.entity.MemberBook;
import com.bob.domain.member.repository.MemberBookRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("회원 도서 Reader 테스트")
@ExtendWith(MockitoExtension.class)
class MemberBookReaderTest {

  @InjectMocks
  private MemberBookReader reader;

  @Mock
  private MemberBookRepository repository;

  @Test
  void 회원_소유_도서_정상_조회() {
    // given
    UUID memberId = UUID.randomUUID();
    MemberBook mb1 = org.mockito.Mockito.mock(MemberBook.class);
    MemberBook mb2 = org.mockito.Mockito.mock(MemberBook.class);
    List<MemberBook> expected = List.of(mb1, mb2);
    given(repository.findByMemberId(memberId)).willReturn(expected);

    // when
    List<MemberBook> actual = reader.readMemberBooksByMemberId(memberId);

    // then
    assertThat(actual).containsExactlyElementsOf(expected);
    then(repository).should().findByMemberId(memberId);
  }

  @Test
  void 회원_소유_도서_정상_빈_리스트() {
    // given
    UUID memberId = UUID.randomUUID();
    given(repository.findByMemberId(memberId)).willReturn(List.of());

    // when
    List<MemberBook> actual = reader.readMemberBooksByMemberId(memberId);

    // then
    assertThat(actual).isEmpty();
    then(repository).should().findByMemberId(memberId);
  }
}