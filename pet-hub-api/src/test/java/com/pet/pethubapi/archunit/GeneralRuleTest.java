package com.pet.pethubapi.archunit;

import com.pet.pethubapi.PetHubApiApplication;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = PetHubApiApplication.class)
public class GeneralRuleTest {

    @ArchTest
    static ArchRule standardStreamRule = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    static ArchRule genericExceptionRule = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    @ArchTest
    static ArchRule useJodaTimeRule = NO_CLASSES_SHOULD_USE_JODATIME;

    @ArchTest
    static ArchRule useJavaLoggingRule = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    @ArchTest
    static ArchRule extendRuntimeExRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Exception")
        .should().beAssignableTo(RuntimeException.class);

    @ArchTest
    static ArchRule apiMethodsReturnResponseEntityRule = ArchRuleDefinition.methods()
        .that().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
        .should().haveRawReturnType(ResponseEntity.class);

    @ArchTest
    static ArchRule noPublicFields = ArchRuleDefinition.noFields()
        .that().areNotStatic().or().areNotFinal()
        .should().bePublic();

}
