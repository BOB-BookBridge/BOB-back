package com.bob.architecture.rule;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaCall;

/**
 * Domain 계층 접근 규칙
 */
public class DomainAccessRules {

    /**
     * Adapter에서 Domain 엔티티의 비즈니스 로직 메서드 호출을 감지하는 Predicate
     *
     * 허용되는 메서드:
     * - getter 메서드 (get*, is*)
     * - Enum 메서드 (name, ordinal, values, valueOf)
     * - Builder 패턴 (builder, build, Builder 클래스의 모든 메서드)
     * - 정적 팩토리 메서드 (of, from, create*)
     * - Query 객체
     */
    public static final DescribedPredicate<JavaCall<?>> callToDomainBusinessLogicMethod() {
        return new DescribedPredicate<>("call to domain business logic method") {
            @Override
            public boolean test(JavaCall<?> call) {
                if (!call.getTargetOwner().getPackageName().contains("core.domain")) {
                    return false;
                }

                String methodName = call.getTarget().getName();

                if (methodName.startsWith("get") || methodName.startsWith("is")) {
                    return false;
                }

                if (methodName.equals("name") || methodName.equals("ordinal") ||
                    methodName.equals("values") || methodName.equals("valueOf")) {
                    return false;
                }

                if (methodName.equals("builder") || methodName.equals("build")) {
                    return false;
                }

                if (methodName.equals("of") || methodName.equals("from") ||
                    methodName.equals("fromIndex") || methodName.startsWith("create")) {
                    return false;
                }

                if (call.getTargetOwner().getSimpleName().contains("Builder")) {
                    return false;
                }

                if (call.getTargetOwner().getPackageName().contains("repository.dsl.query")) {
                    return false;
                }

                return true;
            }
        };
    }

    private DomainAccessRules() {
        // utility class
    }
}
