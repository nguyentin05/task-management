package com.ntt.task_service.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.ntt.task_service", importOptions = ImportOption.DoNotIncludeTests.class)
public class NamingConventionTest {

    @ArchTest
    static final ArchRule controllers_should_be_named_ending_with_Controller = classes()
            .that()
            .areAnnotatedWith(RestController.class)
            .should()
            .haveSimpleNameEndingWith("Controller")
            .because("Tất cả REST controller phải kết thúc bằng 'Controller'");

    @ArchTest
    static final ArchRule services_should_be_named_ending_with_Service = classes()
            .that()
            .areAnnotatedWith(Service.class)
            .should()
            .haveSimpleNameEndingWith("Service")
            .because("Tất cả service class phải kết thúc bằng 'Service'");

    @ArchTest
    static final ArchRule repositories_should_be_named_ending_with_Repository = classes()
            .that()
            .areAnnotatedWith(Repository.class)
            .should()
            .haveSimpleNameEndingWith("Repository")
            .because("Tất cả repository phải kết thúc bằng 'Repository'");

    @ArchTest
    static final ArchRule controllers_should_reside_in_controller_package = classes()
            .that()
            .areAnnotatedWith(RestController.class)
            .should()
            .resideInAPackage("..controller..")
            .because("Controller phải nằm trong package controller");

    @ArchTest
    static final ArchRule services_should_reside_in_service_package = classes()
            .that()
            .areAnnotatedWith(Service.class)
            .should()
            .resideInAPackage("..service..")
            .because("Service phải nằm trong package service");
}
