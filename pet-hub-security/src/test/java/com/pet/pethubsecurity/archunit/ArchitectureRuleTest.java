package com.pet.pethubsecurity.archunit;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import jakarta.persistence.Entity;

@SuppressWarnings("unused")
@AnalyzeClasses(packages = "com.pet.pethubsecurity")
class ArchitectureRuleTest {

    @ArchTest
    public static final ArchRule implementationPackageRule = ArchRuleDefinition.noClasses()
        .should().resideInAPackage("..impl");

    @ArchTest
    public static final ArchRule utilityPackageRule = ArchRuleDefinition.noClasses()
        .should().resideInAPackage("..utils");

    @ArchTest
    public static final ArchRule entityPackageRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(Entity.class)
        .should().resideInAPackage("..domain..");

}
