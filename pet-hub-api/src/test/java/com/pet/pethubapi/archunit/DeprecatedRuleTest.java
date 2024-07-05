package com.pet.pethubapi.archunit;

import com.pet.pethubapi.PetHubApiApplication;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.target;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = PetHubApiApplication.class)
class DeprecatedRuleTest {

    @ArchTest
    static ArchRule deprecatedClassRule = ArchRuleDefinition.noClasses()
        .should().dependOnClassesThat().areAnnotatedWith(Deprecated.class);

    @ArchTest
    static ArchRule deprecatedMethodRule = ArchRuleDefinition.noClasses()
        .should().callMethodWhere(target(annotatedWith(Deprecated.class)));

    @ArchTest
    static ArchRule deprecatedFieldRule = ArchRuleDefinition.noClasses()
        .should().accessFieldWhere(target(annotatedWith(Deprecated.class)));

}
