package com.pet.pethubrabbitmq.archunit;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

@SuppressWarnings("unused")
@AnalyzeClasses(packages = "com.pet.pethubrabbitmq")
class ArchitectureRuleTest {

    @ArchTest
    public static final ArchRule implementationPackageRule = ArchRuleDefinition.noClasses()
        .should().resideInAPackage("..impl");

    @ArchTest
    public static final ArchRule utilityPackageRule = ArchRuleDefinition.noClasses()
        .should().resideInAPackage("..utils");

}
