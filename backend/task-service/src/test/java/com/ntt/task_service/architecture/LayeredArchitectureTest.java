package com.ntt.task_service.architecture;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.ntt.task_service", importOptions = ImportOption.DoNotIncludeTests.class)
public class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule layered_architecture_rule = layeredArchitecture()
            .consideringAllDependencies()
            .withOptionalLayers(true)
            .layer("Controller")
            .definedBy("..controller..")
            .layer("Consumer")
            .definedBy("..consumer..")
            .layer("Service")
            .definedBy("..service..")
            .layer("Repository")
            .definedBy("..repository..")
            .layer("Domain")
            .definedBy("..domain..")
            .layer("DTO")
            .definedBy("..dto..")
            .layer("Mapper")
            .definedBy("..mapper..")
            .layer("Producer")
            .definedBy("..producer..")
            .layer("Scheduler")
            .definedBy("..scheduler..")
            .layer("Validator")
            .definedBy("..validator..")
            .layer("Exception")
            .definedBy("..exception..")
            .layer("Configuration")
            .definedBy("..configuration..")
            .layer("Security")
            .definedBy("..security..")
            .whereLayer("Controller")
            .mayOnlyBeAccessedByLayers("Configuration")
            .whereLayer("Service")
            .mayOnlyBeAccessedByLayers("Controller", "Consumer", "Scheduler", "Configuration")
            .whereLayer("Repository")
            .mayOnlyBeAccessedByLayers("Service", "Scheduler", "Configuration")
            .whereLayer("Domain")
            .mayOnlyBeAccessedByLayers(
                    "Controller",
                    "Service",
                    "Repository",
                    "Mapper",
                    "Producer",
                    "Scheduler",
                    "Configuration",
                    "Security",
                    "DTO")
            .whereLayer("Mapper")
            .mayOnlyBeAccessedByLayers("Service", "Controller", "Consumer")
            .whereLayer("Producer")
            .mayOnlyBeAccessedByLayers("Service", "Scheduler")
            .whereLayer("Validator")
            .mayOnlyBeAccessedByLayers("Controller", "DTO");
}
