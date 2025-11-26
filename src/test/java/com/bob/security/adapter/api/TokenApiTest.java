package com.bob.security.adapter.api;

import static com.bob.core.adapter.common.symbol.ResponseSymbol.OK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bob.security.application.TokenService;

@DisplayName("토큰 재발급 API 테스트")
@ExtendWith(MockitoExtension.class)
class TokenApiTest {

    @InjectMocks
    private TokenApi tokenAuthAdapter;

    @Mock
    private TokenService tokenService;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(tokenAuthAdapter).build();
    }

    @Test
    void 토큰_재발급() throws Exception {
        mvc.perform(post("/auth/token/refresh"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.result").value(OK.name()));

        verify(tokenService, times(1)).reissue(any(), any());
    }
}
