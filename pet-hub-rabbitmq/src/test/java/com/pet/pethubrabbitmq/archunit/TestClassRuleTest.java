package com.pet.pethubrabbitmq.archunit;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;

@SuppressWarnings("unused")
@AnalyzeClasses(packages = "com.pet.pethubrabbitmq")
class TestClassRuleTest {

    @ArchTest
    public static final ArchRule testClassAccessRule = ArchRuleDefinition.classes()
        .that().containAnyMethodsThat(annotatedWith(Test.class))
        .should().bePackagePrivate();

    @ArchTest
    public static final ArchRule testMethodAccessRule = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(Test.class)
        .should().bePackagePrivate();

    @ArchTest
    public static final ArchRule noJunit4AssertRule = ArchRuleDefinition.noClasses()
        .should().dependOnClassesThat().haveFullyQualifiedName("org.junit.Assert");

    @ArchTest
    public static final ArchRule noJunit5AssertRule = ArchRuleDefinition.noClasses()
        .should().dependOnClassesThat().haveFullyQualifiedName("org.junit.jupiter.api.Assertions");

    @ArchTest
    public static final ArchRule noDisabledTestRule = ArchRuleDefinition.noClasses()
        .should().beAnnotatedWith(Disabled.class);

    @ArchTest
    public static final ArchRule noTransactionTestRule = ArchRuleDefinition.noClasses()
        .should().beAnnotatedWith(Transactional.class);

}
