package com.ntt.api_gateway.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.ntt.api_gateway",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
public class GatewayArchitectureTest {

    @ArchTest
    static final ArchRule no_business_logic_in_gateway = noClasses()
            .should().beAnnotatedWith(Service.class)
            .orShould().beAnnotatedWith(Repository.class)
            .because("API Gateway chỉ dùng để Routing/Filtering, cấm viết Business Logic ở đây");

    @ArchTest
    static final ArchRule filters_should_be_in_correct_package_and_named = classes()
            .that().implement(org.springframework.cloud.gateway.filter.GlobalFilter.class)
            .or().implement(org.springframework.cloud.gateway.filter.GatewayFilter.class)
            .should().resideInAPackage("..filter..")
            .andShould().haveSimpleNameEndingWith("Filter")
            .because("Các bộ lọc của Gateway phải nằm trong package filter và có hậu tố Filter");

    @ArchTest
    static final ArchRule no_webmvc_dependencies = noClasses()
            .should().dependOnClassesThat().resideInAPackage("org.springframework.web.servlet..")
            .orShould().dependOnClassesThat().resideInAPackage("jakarta.servlet..")
            .because("API Gateway chạy trên WebFlux, tuyệt đối không import thư viện của WebMVC (như HttpServletRequest)");
}