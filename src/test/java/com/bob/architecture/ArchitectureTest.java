package com.bob.architecture;

import static com.bob.architecture.rule.DomainAccessRules.callToDomainBusinessLogicMethod;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * 아키텍처 검증
 *
 * BOB 아키텍처 구조:
 * - security, admin: 독립적인 계층
 * - core.{module}.adapter: 프레젠테이션 계층 (Inbound Adapter - REST API)
 * - core.{module}.application: 애플리케이션 서비스 계층
 *   - port.in: Inbound Port (외부로부터 들어오는 요청)
 *   - port.out: Outbound Port (다른 도메인/인프라로 나가는 요청)
 * - core.{module}.domain: 도메인 모델 계층 (비즈니스 로직)
 * - integration: 도메인 간 통합 어댑터 (Outbound Adapter)
 * - infrastructure: 인프라 어댑터 (Outbound Adapter - 메일, 캐시, 스토리지 등)
 */
@AnalyzeClasses(packages = "com.bob", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    // ================================
    // 규칙 : 표현 계층에 해당하는 Adapter와 Security는 Application 서비스 구현체를 직접 의존하면 안됨 (Inbound Port 사용)
    // ================================

    @ArchTest
    public static final ArchRule adapter_should_not_depend_on_service =
        noClasses()
            .that()
            .resideInAPackage("..core.*.adapter..")
            .should()
            .dependOnClassesThat()
            .haveNameMatching(".*(Service)$")
            .because("Adapter는 Application 서비스 구현체가 아닌 Inbound Port 인터페이스 의존");

    // ================================
    // 규칙 : Application은 Infrastructure와 다른 도메인에 직접 의존할 수 없음 (Outbound Port 사용)
    // ================================

    @ArchTest
    public static final ArchRule application_should_not_depend_on_outer_layers =
        noClasses()
            .that()
            .resideInAPackage("..core.*.application..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..core.*.adapter..", "..infrastructure..", "..integration..")
            .because("Application은 외부 계층(Adapter, Infrastructure, Integration)에 의존 금지. Outbound Port만 사용");

    @ArchTest
    public static final ArchRule adapter_should_only_call_getters_on_domain_entities =
        noClasses()
            .that()
            .resideInAPackage("..core.*.adapter..")
            .should()
            .callCodeUnitWhere(callToDomainBusinessLogicMethod())
            .because("Adapter는 Domain 엔티티의 getter/is 메서드만 호출 가능. 비즈니스 로직은 Application 계층에서 실행");

    // ================================
    // 규칙 : Domain은 상위 계층(Adapter, Application, Infrastructure, Integration)에 의존할 수 없음
    // ================================

    @ArchTest
    public static final ArchRule domain_should_not_depend_on_upper_layers =
        noClasses()
            .that()
            .resideInAPackage("..core.*.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..core.*.adapter..", "..core.*.application..", "..infrastructure..", "..integration..")
            .because("Domain은 상위 계층(Adapter, Application, Infrastructure, Integration)에 의존 금지");

    @ArchTest
    public static final ArchRule domain_should_not_depend_on_servlet_api =
        noClasses()
            .that()
            .resideInAPackage("..core.*.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("jakarta.servlet..")
            .because("Domain은 Servlet에 의존 금지");

    // ================================
    // 규칙 : 의존성은 단방향 (순환 의존성 금지)
    // ================================

    @ArchTest
    public static final ArchRule domains_should_not_have_cycles =
        slices()
            .matching("..core.(*).domain..")
            .should()
            .beFreeOfCycles()
            .because("Domain 간 순환 의존성 발생 금지");

    @ArchTest
    public static final ArchRule application_domains_should_not_have_cycles =
        slices()
            .matching("..core.(*).application..")
            .should()
            .beFreeOfCycles()
            .because("Application 간 순환 의존성 발생 금지, Application 간 상호작용은 Outbound Port를 통해서만 이루어짐");

    @ArchTest
    public static final ArchRule layers_should_not_have_cycles =
        slices()
            .matching("..core.(*).(domain|application|adapter)..")
            .should()
            .beFreeOfCycles()
            .because("계층 간 순환 의존성 발생 금지");

    // ================================
    // 규칙 : 도메인 간 상호작용은 Outbound Port를 통해, 구현체는 Integration에 위치
    // ================================

    @ArchTest
    public static final ArchRule integration_adapters_should_implement_outbound_ports =
        classes()
            .that()
            .resideInAPackage("..integration.adapter..")
            .and()
            .haveSimpleNameEndingWith("Adapter")
            .should()
            .implement(resideInAPackage("..core.*.application..port.out.."))
            .because("Integration 어댑터는 도메인 간 상호작용을 위한 Outbound Port를 구현");

    // ================================
    // 명명 규칙 및 패턴
    // ================================

    @ArchTest
    public static final ArchRule services_should_be_in_application_layer =
        classes()
            .that()
            .haveSimpleNameEndingWith("Service")
            .should()
            .resideInAnyPackage("..*.application..")
            .because("Service는 Application 계층에 위치");

    @ArchTest
    public static final ArchRule services_should_implement_inbound_ports =
        classes()
            .that()
            .haveNameMatching(".*(Service)$")
            .and()
            .resideInAPackage("..core.*.application..")
            .should()
            .implement(resideInAPackage("..core.*.application..port.in.."))
            .because("Service는 Inbound Port를 구현");

    @ArchTest
    public static final ArchRule adapters_should_not_be_in_application_layer =
        noClasses()
            .that()
            .haveSimpleNameEndingWith("Adapter")
            .should()
            .resideInAPackage("..core.*.application..")
            .because("Adapter는 Application 계층이 아닌 security || integration || infrastructure에 위치");

    @ArchTest
    public static final ArchRule infrastructure_adapters_should_implement_outbound_ports =
        classes()
            .that()
            .resideInAPackage("..infrastructure..adapter..")
            .and()
            .haveSimpleNameEndingWith("Adapter")
            .should()
            .implement(resideInAPackage("..application..port.out.."))
            .because("Infrastructure 어댑터는 Outbound Port를 구현해야 합니다.");

    // ================================
    // 계층 간 Adapter 정의
    // ================================

    @ArchTest
    public static final ArchRule core_adapter_out_should_not_implement_core_outbound_ports =
        noClasses()
            .that()
            .resideInAPackage("..core.*.adapter..out..")
            .should()
            .implement(resideInAPackage("..core.*.application..port.out.."))
            .because("core 도메인 간 협력은 integration 패키지에서 구현. 외부 모듈의 Outbound Port는 구현 가능");
}
