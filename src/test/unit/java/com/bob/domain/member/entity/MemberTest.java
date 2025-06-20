package com.bob.domain.member.entity;

import static com.bob.support.fixture.domain.MemberFixture.defaultIdMember;
import static com.bob.support.fixture.domain.MemberFixture.defaultMember;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("회원 도메인 테스트")
class MemberTest {

  @Test
  @DisplayName("비밀번호 수정 테스트")
  void 비밀번호를_수정할_수_있다() {
    // given
    Member member = defaultIdMember();

    // when
    member.updatePassword("newPassword");

    // then
    assertThat(member.getPassword()).isNotEqualTo(defaultMember().getPassword());
    assertThat(member.getPassword()).isEqualTo("newPassword");
  }

  @Test
  @DisplayName("닉네임 수정 테스트")
  void 닉네임을_수정할_수_있다() {
    // given
    Member member = defaultIdMember();

    // when
    member.updateNickname("new-nickname");

    // then
    assertThat(member.getNickname()).isNotEqualTo(defaultMember().getNickname());
    assertThat(member.getNickname()).isEqualTo("new-nickname");
  }

  @ParameterizedTest(name = "[{index}] 기존 별명: 'tester', 새로운 별명 : {0}")
  @CsvSource(value = {
      "other-nickname,false", // 기존, 새로운 별명이 다른 경우
      "tester,true",          // 기존, 새로운 별명이 같은 경우
      "null,false"            // 새로운 별명이 null 인 경우
  }, nullValues = "null")
  @DisplayName("입력받은_별명에_따른_반환값_비교")
  void 입력받은_별명에_따른_반환값_비교(String input, boolean expected) {
    // given
    Member member = defaultIdMember();

    // when
    boolean result = member.isEqualsNickname(input);

    // then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  @DisplayName("프로필 이미지 URL 수정 테스트")
  void 프로필_이미지_URL을_수정할_수_있다() {
    // given
    Member member = defaultIdMember();
    String newProfileImageUrl = "profile/test.jpg";

    // when
    member.updateProfileImageUrl(newProfileImageUrl);

    // then
    assertThat(member.getProfileImageUrl()).isEqualTo(newProfileImageUrl);
  }
}