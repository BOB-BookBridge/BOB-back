package com.bob.infrastructure.mail.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@DisplayName("메일 html 렌더링 테스트")
@ExtendWith(MockitoExtension.class)
class MailTemplateRendererTest {

    @InjectMocks
    MailTemplateRenderer mailTemplateRenderer;

    @Mock
    TemplateEngine templateEngine;

    @Test
    void HTML_렌더링() {
        String templateName = "test-html";
        Map<String, Object> variables = Map.of("subject", "테스트");

        given(templateEngine.process(anyString(), any(Context.class))).willReturn("<html>test html template</html>");

        String result = mailTemplateRenderer.render(templateName, variables);

        ArgumentCaptor<String> templateCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);

        then(templateEngine).should().process(templateCaptor.capture(), contextCaptor.capture());

        assertThat(templateCaptor.getValue()).isEqualTo("mail/" + templateName);

        Context capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.getVariable("subject")).isEqualTo("테스트");

        assertThat(result).isEqualTo("<html>test html template</html>");
    }
}
