package com.bob.domain.file.entity;

import static com.bob.global.exception.response.ApplicationError.UN_SUPPORTED_TYPE;
import static com.bob.support.fixture.domain.FileFixture.defaultFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bob.domain.file.entity.type.FileDomain;
import com.bob.global.exception.exceptions.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("파일 도메인 테스트")
class FileDomainTest {

  @Test
  @DisplayName("파일 참조 수정 테스트")
  void 파일의_referenceId를_수정할_수_있다() {
    // given
    File file = defaultFile("post/1.jpg", 0, "1");

    // when
    file.mappingDomainId(0, "new");

    // then
    assertThat(file.getSequence()).isEqualTo(0);
    assertThat(file.getReferenceId()).isEqualTo("new");
  }

  @Test
  @DisplayName("파일 도메인 값 확인")
  void 파일_도메인_enum_값을_확인할_수_있다() {
    // when & then
    assertThat(FileDomain.valueOf("POST")).isEqualTo(FileDomain.POST);
    assertThat(FileDomain.valueOf("CHAT")).isEqualTo(FileDomain.CHAT);
  }

  @Test
  @DisplayName("FileDomain from 메서드 - 대소문자 구분 없이 변환 가능")
  void 파일_도메인_from_메서드는_대소문자_구분없이_변환할_수_있다() {
    assertThat(FileDomain.from("post")).isEqualTo(FileDomain.POST);
    assertThat(FileDomain.from("CHAT")).isEqualTo(FileDomain.CHAT);
  }

  @Test
  @DisplayName("FileDomain from 메서드 - 존재하지 않는 값이면 예외 발생")
  void 파일_도메인_from_메서드는_존재하지_않는_값이면_예외를_발생시킨다() {
    assertThatThrownBy(() -> FileDomain.from("invalid"))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(UN_SUPPORTED_TYPE.getMessage());
  }

  @Test
  @DisplayName("파일 도메인 전체 values 확인")
  void 파일_도메인_values_확인() {
    // when
    FileDomain[] values = FileDomain.values();

    // then
    assertThat(values).containsExactly(FileDomain.POST, FileDomain.CHAT);
  }
}