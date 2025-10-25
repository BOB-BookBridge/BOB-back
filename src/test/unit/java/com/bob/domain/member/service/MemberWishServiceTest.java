package com.bob.domain.member.service;

import static com.bob.global.exception.response.ApplicationError.MEMBER_WISH_DUPLICATED;
import static com.bob.support.fixture.command.CreateMemberWishCommandFixture.DEFAULT_CREATE_WISH_COMMAND;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.bob.domain.member.entity.MemberWish;
import com.bob.domain.member.repository.MemberWishRepository;
import com.bob.domain.member.service.dto.command.CreateMemberWishCommand;
import com.bob.domain.member.service.port.out.MemberBookPort;
import com.bob.global.exception.exceptions.ApplicationException;
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
}