package com.bob.domain.member.service;

import static com.bob.global.exception.response.ApplicationError.MEMBER_WISH_DUPLICATED;
import static com.bob.global.exception.response.ApplicationError.NOT_EXIST_OBJECT;
import static com.bob.global.exception.response.ApplicationError.OBJECT_ACCESS_DENIED;
import static com.bob.support.fixture.command.CreateMemberWishCommandFixture.DEFAULT_CREATE_WISH_COMMAND;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.domain.MemberWishFixture.DEFAULT_MEMBER_WISH;
import static com.bob.support.fixture.domain.MemberWishFixture.DEFAULT_MEMBER_WISHES;
import static com.bob.support.fixture.response.BookResponseFixture.DEFAULT_BOOK_RESPONSES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.bob.domain.member.entity.MemberWish;
import com.bob.domain.member.repository.MemberWishRepository;
import com.bob.domain.member.service.dto.command.CreateMemberWishCommand;
import com.bob.domain.member.service.dto.command.DeleteMemberWishCommand;
import com.bob.domain.member.service.dto.query.ReadMemberWishesQuery;
import com.bob.domain.member.service.dto.response.MemberWishesResult;
import com.bob.domain.member.service.port.out.MemberBookPort;
import com.bob.global.exception.exceptions.ApplicationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("회원 희망 도서 서비스 테스트")
class MemberWishServiceTest {

  @InjectMocks
  MemberWishService service;

  @Mock
  MemberWishRepository repository;

  @Mock
  MemberBookPort bookPort;

  @Test
  void 희망_도서_생성() {
    // given
    UUID memberId = MEMBER_ID;
    Long bookId = 1L;
    CreateMemberWishCommand command = DEFAULT_CREATE_WISH_COMMAND;
    given(bookPort.create(any(), any(), any(), any(), any(), any(), any())).willReturn(bookId);
    given(repository.existsByMemberIdAndBookId(memberId, bookId)).willReturn(false);

    // when
    service.createMemberWishProcess(command);

    // then
    then(bookPort).should(times(1)).create(any(), any(), any(), any(), any(), any(), any());
    then(repository).should(times(1)).existsByMemberIdAndBookId(memberId, bookId);
    then(repository).should(times(1)).save(any(MemberWish.class));
  }

  @Test
  void 희망_도서_생성_시_중복되는_품목이면_예외가_발생한다() {
    // given
    UUID memberId = MEMBER_ID;
    Long bookId = 1L;
    CreateMemberWishCommand command = DEFAULT_CREATE_WISH_COMMAND;
    given(bookPort.create(any(), any(), any(), any(), any(), any(), any())).willReturn(bookId);
    given(repository.existsByMemberIdAndBookId(memberId, bookId)).willReturn(true);

    // when & then
    assertThatThrownBy(() -> service.createMemberWishProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(MEMBER_WISH_DUPLICATED.getMessage());

    then(repository).should(never()).save(any(MemberWish.class));
  }

  @Test
  void 희망_도서_목록_조회() {
    // given
    UUID memberId = MEMBER_ID;
    ReadMemberWishesQuery query = ReadMemberWishesQuery.of(memberId);
    given(repository.findAllByMemberId(memberId)).willReturn(DEFAULT_MEMBER_WISHES);
    given(bookPort.readBookSummaries(List.of(1L, 2L))).willReturn(DEFAULT_BOOK_RESPONSES);

    // when
    MemberWishesResult result = service.readWishesProcess(query);

    // then
    then(repository).should(times(1)).findAllByMemberId(memberId);
    then(bookPort).should(times(1)).readBookSummaries(List.of(1L, 2L));

    assertThat(result).isNotNull();
    assertThat(result.wishes()).hasSize(2);
    assertThat(result.wishes().get(0).id()).isEqualTo(1L);
    assertThat(result.wishes().get(1).id()).isEqualTo(2L);
  }

  @Test
  void 희망_도서_삭제() {
    // given
    UUID ownerId = MEMBER_ID;
    Long wishId = 1L;
    DeleteMemberWishCommand command = new DeleteMemberWishCommand(ownerId, wishId);
    given(repository.findById(wishId)).willReturn(Optional.of(DEFAULT_MEMBER_WISH()));

    // when
    service.deleteWishProcess(command);

    // then
    then(repository).should(times(1)).findById(wishId);
    then(repository).should(times(1)).delete(any(MemberWish.class));
  }

  @Test
  void 희망_도서_삭제_시_소유자가_아니면_예외가_발생한다() {
    // given
    Long wishId = 1L;
    DeleteMemberWishCommand command = new DeleteMemberWishCommand(OTHER_MEMBER_ID, wishId);
    given(repository.findById(wishId)).willReturn(Optional.of(DEFAULT_MEMBER_WISH())); // ownerId = MEMBER_ID

    // when & then
    assertThatThrownBy(() -> service.deleteWishProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(OBJECT_ACCESS_DENIED.getMessage());

    then(repository).should(times(1)).findById(wishId);
    then(repository).should(never()).delete(any(MemberWish.class));
  }

  @Test
  void 희망_도서_삭제_시_데이터가_존재하지_않으면_예외가_발생한다() {
    // given
    UUID requesterId = MEMBER_ID;
    Long wishId = -1L;
    DeleteMemberWishCommand command = new DeleteMemberWishCommand(requesterId, wishId);
    given(repository.findById(wishId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> service.deleteWishProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(NOT_EXIST_OBJECT.getMessage());

    then(repository).should(times(1)).findById(wishId);
    then(repository).should(never()).delete(any(MemberWish.class));
  }
}