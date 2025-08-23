package com.bob.infra.auth.service;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.defaultIdMember;
import static com.bob.support.fixture.domain.MemberFixture.otherMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.bob.domain.member.entity.Member;
import com.bob.domain.member.repository.MemberRepository;
import com.bob.infra.auth.response.MemberDetails;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@DisplayName("MemberDetailsService 테스트")
@ExtendWith(MockitoExtension.class)
class MemberDetailsServiceTest {

  @InjectMocks
  private MemberDetailsService memberDetailsService;

  @Mock
  private MemberRepository memberRepository;

  @Test
  void 정상_회원_조회_테스트() {
    // given
    Member member = defaultIdMember();
    memberRepository.save(member);
    given(memberRepository.findByEmail("test@email.com")).willReturn(Optional.of(member));

    // when
    MemberDetails memberDetails = (MemberDetails) memberDetailsService.loadUserByUsername(member.getEmail());

    // then
    assertThat(memberDetails).isNotNull();
    assertThat(memberDetails.id()).isEqualTo(MEMBER_ID);
    assertThat(memberDetails.email()).isEqualTo(member.getEmail());
    assertThat(memberDetails.password()).isEqualTo(member.getPassword());
    assertThat(memberDetails.enabled()).isTrue();
  }

  @Test
  void 탈퇴_회원_조회_테스트() {
    // given
    Member member = defaultIdMember();
    member.updateRemoveStatus(true);
    memberRepository.save(member);
    given(memberRepository.findByEmail("test@email.com")).willReturn(Optional.of(member));

    // when
    MemberDetails memberDetails = (MemberDetails) memberDetailsService.loadUserByUsername(member.getEmail());

    // then
    assertThat(memberDetails).isNotNull();
    assertThat(memberDetails.id()).isEqualTo(MEMBER_ID);
    assertThat(memberDetails.enabled()).isFalse(); // 로그인 필터에서 DisabledException 처리
  }

  @Test
  void 존재하지_않는_회원_조회_테스트() {
    // given
    given(memberRepository.findByEmail("unknown@email.com")).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> memberDetailsService.loadUserByUsername(otherMember().getEmail()))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage(otherMember().getEmail());
  }
}
