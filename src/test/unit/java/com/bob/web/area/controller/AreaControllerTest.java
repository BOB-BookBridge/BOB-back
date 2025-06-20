package com.bob.web.area.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.bob.domain.area.service.dto.command.AuthenticationCommand;
import com.bob.domain.area.service.AreaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("활동 지역 인증 API 테스트")
@ExtendWith(MockitoExtension.class)
class AreaControllerTest {

  @InjectMocks
  private AreaController areaController;

  @Mock
  private AreaService areaService;

  @Test
  @DisplayName("활동 지역 인증 API 호출 테스트")
  void 활동지역_인증_API를_호출할_수_있다() throws Exception {
    // given
    MockMvc mvc = standaloneSetup(areaController).build();
    String json = """
        {
          "emdId": 213,
          "lat": 37.5012743,
          "lon": 127.039585,
          "purpose": "SIGN_UP"
        }
        """;

    // when
    mvc.perform(patch("/areas/authentication")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json)
    ).andExpect(status().isOk());

    // then
    verify(areaService, times(1)).authenticateProcess(any(AuthenticationCommand.class));
  }
}