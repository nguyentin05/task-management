package com.ntt.authentication.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.ntt.authentication", importOptions = ImportOption.DoNotIncludeTests.class)
public class ControllerComponentsTest {

    @ArchTest
    static final ArchRule controllers_should_not_depend_on_repository =
            noClasses()
                    .that().areAnnotatedWith(RestController.class)
                    .should().dependOnClassesThat().resideInAPackage("..repository..")
                    .because("Controller không được gọi trực tiếp Repository — phải qua Service");

    @ArchTest
    static final ArchRule controllers_should_not_depend_on_domain =
            noClasses()
                    .that().areAnnotatedWith(RestController.class)
                    .should().dependOnClassesThat().resideInAPackage("..domain..")
                    .because("Controller chỉ làm việc với DTO, không trực tiếp với Domain entity");

    @ArchTest
    static final ArchRule external_controllers_in_correct_package =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .and().haveSimpleNameNotEndingWith("InternalAuthenticationController")
                    .should().resideInAPackage("..controller.external..")
                    .because("External controller phải nằm trong package controller.external");
}