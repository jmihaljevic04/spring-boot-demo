package com.pet.pethubapi;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

@AnalyzeClasses(packagesOf = PetHubApiApplication.class)
class PetHubArchitectureShould {

    @ArchTest
    @SuppressWarnings("unused")
    static ArchRule domainRule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("com.pet.pethubapi.domain..")
        .should().dependOnClassesThat()
        .resideInAnyPackage("com.pet.pethubapi.application..", "com.pet.pethubapi.infrastructure..", "com.pet.pethubapi.interfaces..");

    @ArchTest
    @SuppressWarnings("unused")
    static ArchRule applicationRule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("com.pet.pethubapi.application..")
        .should().dependOnClassesThat()
        .resideInAnyPackage("com.pet.pethubapi.infrastructure..", "com.pet.pethubapi.interfaces..");

    @ArchTest
    @SuppressWarnings("unused")
    static ArchRule interfaceRule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("com.pet.pethubapi.interfaces..")
        .should().dependOnClassesThat()
        .resideInAnyPackage("com.pet.pethubapi.infrastructure..");

}
