package com.ntt.comment_service.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.ntt.comment_service", importOptions = ImportOption.DoNotIncludeTests.class)
public class DomainComponentsTest {

    @ArchTest
    static final ArchRule domain_should_not_depend_on_controller = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..controller..")
            .because("Domain không được phụ thuộc vào Controller layer");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_service = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .areAnnotatedWith(Service.class)
            .because("Domain không được phụ thuộc vào Service layer");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_dto = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..dto..")
            .because("Domain không được phụ thuộc vào DTO — tránh coupling với API layer");

    @ArchTest
    static final ArchRule domain_classes_should_not_have_spring_annotations = classes()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .notBeAnnotatedWith(RestController.class)
            .andShould()
            .notBeAnnotatedWith(Service.class)
            .because("Domain entity không được có Spring annotation — giữ domain thuần túy");
}
