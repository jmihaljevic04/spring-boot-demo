package com.pet.pethubapi.archunit;

import com.pet.pethubapi.PetHubApiApplication;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = PetHubApiApplication.class)
public class TestClassRuleTest {

    @ArchTest
    static ArchRule testClassAccessRule = ArchRuleDefinition.classes()
        .that().containAnyMethodsThat(annotatedWith(Test.class))
        .should().bePackagePrivate();

    @ArchTest
    static ArchRule testMethodAccessRule = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(Test.class)
        .should().bePackagePrivate();

    @ArchTest
    static ArchRule noJunit4AssertRule = ArchRuleDefinition.noClasses()
        .should().dependOnClassesThat().haveFullyQualifiedName("org.junit.Assert");

    @ArchTest
    static ArchRule noJunit5AssertRule = ArchRuleDefinition.noClasses()
        .should().dependOnClassesThat().haveFullyQualifiedName("org.junit.jupiter.api.Assertions");

    @ArchTest
    static ArchRule noDisabledTestRule = ArchRuleDefinition.noClasses()
        .should().beAnnotatedWith(Disabled.class);

}
