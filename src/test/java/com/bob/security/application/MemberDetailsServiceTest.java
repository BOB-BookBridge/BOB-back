package com.bob.security.application;

import static com.bob.support.fixture.auth.port.dto.AuthMemberFixture.createActiveAuthMember;
import static com.bob.support.fixture.auth.port.dto.AuthMemberFixture.createDeactivatedAuthMember;
import static com.bob.support.fixture.member.domain.MemberFixture.createOtherMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bob.security.application.port.dto.AuthMember;
import com.bob.security.application.port.out.MemberLoader;
import com.bob.security.model.MemberDetails;

@DisplayName("MemberDetailsService 테스트")
@ExtendWith(MockitoExtension.class)
class MemberDetailsServiceTest {

    @InjectMocks
    private MemberDetailsService memberDetailsService;

    @Mock
    private MemberLoader memberLoader;

    @Test
    void 회원_조회() {
        AuthMember authMember = createActiveAuthMember();
        given(memberLoader.load("test@email.com")).willReturn(Optional.of(authMember));

        MemberDetails memberDetails = (MemberDetails)memberDetailsService.loadUserByUsername("test@email.com");

        assertThat(memberDetails).isNotNull();
        assertThat(memberDetails.id()).isEqualTo(authMember.id());
        assertThat(memberDetails.email()).isEqualTo(authMember.email());
        assertThat(memberDetails.password()).isEqualTo(authMember.password());
        assertThat(memberDetails.enabled()).isTrue();
    }

    @Test
    void 탈퇴_회원_조회() {
        AuthMember deactivatedMember = createDeactivatedAuthMember();
        given(memberLoader.load("test@email.com")).willReturn(Optional.of(deactivatedMember));

        MemberDetails memberDetails = (MemberDetails)memberDetailsService.loadUserByUsername("test@email.com");

        assertThat(memberDetails).isNotNull();
        assertThat(memberDetails.id()).isEqualTo(deactivatedMember.id());
        assertThat(memberDetails.enabled()).isFalse(); // 로그인 필터에서 DisabledException 처리
    }

    @Test
    void 회원_조회_시_존재하지_않는_회원이면_예외가_발생한다() {
        assertThatThrownBy(() -> memberDetailsService.loadUserByUsername(createOtherMember().getEmail()))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessage(createOtherMember().getEmail());
    }
}
