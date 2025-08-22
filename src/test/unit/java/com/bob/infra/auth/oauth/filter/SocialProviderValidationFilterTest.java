package com.bob.infra.auth.oauth.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("provider 검증 필터 테스트")
@ExtendWith(MockitoExtension.class)
class SocialProviderValidationFilterTest {

  @InjectMocks
  private SocialProviderValidationFilter filter;

  @Mock
  private ClientRegistrationRepository clientRegistrationRepository;

  private FilterChain chain;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  void setUp() {
    chain = mock(FilterChain.class);
    ReflectionTestUtils.setField(filter, "baseUrl", "https://base");
    ReflectionTestUtils.setField(filter, "entrance", "/oauth2/authorization");
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
  void 등록_provider() throws Exception {
    // given
    request.setServletPath("/oauth2/authorization/google");
    ClientRegistration dummy = mock(ClientRegistration.class);
    given(clientRegistrationRepository.findByRegistrationId("google")).willReturn(dummy);

    // when
    filter.doFilter(request, response, chain);

    // then
    then(clientRegistrationRepository).should().findByRegistrationId("google");
    then(chain).should().doFilter(request, response);
  }

  @Test
  void 미등록_provider() throws Exception {
    // given
    request.setServletPath("/oauth2/authorization/kakao");
    given(clientRegistrationRepository.findByRegistrationId("kakao")).willReturn(null);

    // when
    filter.doFilter(request, response, chain);

    // then
    then(clientRegistrationRepository).should().findByRegistrationId("kakao");
    then(chain).should(never()).doFilter(request, response);

    // Redirect 확인
    assertThat(response.getStatus()).isEqualTo(302);
    assertThat(response.getRedirectedUrl()).isEqualTo("https://base/error?cause=unsupported_provider_kakao");
  }

  @Test
  void 필터생략() throws Exception {
    // given
    request.setServletPath("/domain/id");

    // when
    filter.doFilter(request, response, chain);

    // then
    then(clientRegistrationRepository).should(never()).findByRegistrationId(org.mockito.ArgumentMatchers.any());
    then(chain).should().doFilter(request, response);
  }
}