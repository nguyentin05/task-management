package com.ntt.notification_service.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
        packages = "com.ntt.notification_service",
        importOptions = {ImportOption.DoNotIncludeTests.class})
public class NotificationArchitectureTest {

    @ArchTest
    static final ArchRule event_driven_architecture = layeredArchitecture()
            .consideringAllDependencies()
            .withOptionalLayers(true)
            .layer("Consumer")
            .definedBy("..consumer..")
            .layer("Service")
            .definedBy("..service..")
            .layer("ExternalClient")
            .definedBy("..client..")
            .layer("Configuration")
            .definedBy("..configuration..")
            .whereLayer("Service")
            .mayOnlyBeAccessedByLayers("Consumer", "Configuration")
            .whereLayer("ExternalClient")
            .mayOnlyBeAccessedByLayers("Service", "Configuration");

    @ArchTest
    static final ArchRule should_not_have_controllers_or_repositories = noClasses()
            .should()
            .beAnnotatedWith(RestController.class)
            .orShould()
            .beAnnotatedWith(Repository.class)
            .because("Notification Service là Event-Driven, không nhận HTTP Request và không kết nối Database")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule clients_should_be_named_properly = classes()
            .that()
            .resideInAPackage("..client..")
            .should()
            .haveSimpleNameEndingWith("Client")
            .because("Các class gọi API bên ngoài phải có đuôi là Client (VD: BrevoClient)")
            .allowEmptyShould(true);
}
