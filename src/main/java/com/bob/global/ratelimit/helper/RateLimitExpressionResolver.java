package com.bob.global.ratelimit.helper;

import jakarta.servlet.http.HttpServletRequest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RateLimitExpressionResolver {

    private static final ExpressionParser expressionParser = new SpelExpressionParser();

    public static String resolveExpression(String expression, JoinPoint joinPoint, HttpServletRequest request) {
        if (expression == null || expression.trim().isEmpty())
            return null;

        try {
            EvaluationContext context = createEvaluationContext(joinPoint, request);
            Object result = expressionParser.parseExpression(expression).getValue(context);

            if (result != null)
                return result.toString();

            log.warn("Expression evaluated to null: {}", expression);

            return null;
        } catch (Exception e) {
            log.error("Failed to resolve expression: {}, error: {}", expression, e.getMessage(), e);

            return null;
        }
    }

    private static EvaluationContext createEvaluationContext(JoinPoint joinPoint, HttpServletRequest request) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        addMethodParameters(context, joinPoint);

        return context;
    }

    private static void addMethodParameters(StandardEvaluationContext context, JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (parameterNames != null && args != null && parameterNames.length == args.length) {
            for (int i = 0; i < parameterNames.length; i++)
                context.setVariable(parameterNames[i], args[i]);
        }
    }

    public static boolean hasExpression(String expression) {
        return expression != null && !expression.trim().isEmpty();
    }
}
