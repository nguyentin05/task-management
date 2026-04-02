package com.ntt.task_service.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

@AnalyzeClasses(packages = "com.ntt.task_service", importOptions = ImportOption.DoNotIncludeTests.class)
public class GeneralCodingRulesTest {

    @ArchTest
    static final ArchRule no_field_injection =
            NO_CLASSES_SHOULD_USE_FIELD_INJECTION
                    .because("Dùng constructor injection (@RequiredArgsConstructor) thay vì @Autowired field");

    @ArchTest
    static final ArchRule no_java_util_logging =
            NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING
                    .because("Dùng SLF4J + Logback thay vì java.util.logging");

    @ArchTest
    static final ArchRule no_system_out_println =
            noClasses()
                    .should().callMethod(System.class, "println", String.class)
                    .because("Dùng logger thay vì System.out.println");

    @ArchTest
    static final ArchRule no_generic_exceptions =
            NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS
                    .because("Throw AppException với ErrorCode cụ thể thay vì Exception/RuntimeException chung");
}