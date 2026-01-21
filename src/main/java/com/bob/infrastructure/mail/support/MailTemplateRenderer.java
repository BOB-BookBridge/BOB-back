package com.bob.infrastructure.mail.support;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailTemplateRenderer {

    private final TemplateEngine templateEngine;

    public String render(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process("mail/" + templateName, context);
    }
}
