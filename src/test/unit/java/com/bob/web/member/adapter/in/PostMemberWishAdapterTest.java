package com.bob.web.member.adapter.in;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.MemberWishesResultFixture.DEFAULT_MEMBER_WISHES_RESULT;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.member.service.dto.query.ReadMemberWishesQuery;
import com.bob.domain.member.service.dto.response.MemberWishesResult;
import com.bob.domain.member.usecase.MemberWishReadUseCase;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("PostMemberWishAdapter 테스트")
@ExtendWith(MockitoExtension.class)
class PostMemberWishAdapterTest {

  @InjectMocks
  private PostMemberWishAdapter adapter;

  @Mock
  private MemberWishReadUseCase readUseCase;

  @Test
  void 회원_희망_도서_존재_확인_기능_호출() {
    // given
    UUID memberId = MEMBER_ID;
    MemberWishesResult result = DEFAULT_MEMBER_WISHES_RESULT;
    given(readUseCase.readWishesProcess(ReadMemberWishesQuery.of(memberId))).willReturn(result);

    // when
    boolean exists = adapter.exists(memberId);

    // then
    assertThat(exists).isTrue();
    then(readUseCase).should().readWishesProcess(ReadMemberWishesQuery.of(memberId));
  }

  @Test
  void 회원_희망_도서_미존재_시_false_반환() {
    // given
    UUID memberId = MEMBER_ID;
    MemberWishesResult empty = MemberWishesResult.from(emptyList());
    given(readUseCase.readWishesProcess(ReadMemberWishesQuery.of(memberId))).willReturn(empty);

    // when
    boolean exists = adapter.exists(memberId);

    // then
    assertThat(exists).isFalse();
    then(readUseCase).should().readWishesProcess(ReadMemberWishesQuery.of(memberId));
  }
}
