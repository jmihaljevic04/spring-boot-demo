package com.pet.pethubapi.archunit;

import com.pet.pethubapi.PetHubApiApplication;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import jakarta.persistence.Entity;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = PetHubApiApplication.class)
class ArchitectureRuleTest {

    @ArchTest
    static ArchRule domainLayerRule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat()
        .resideInAnyPackage("..application..", "..infrastructure..", "..interfaces..");

    @ArchTest
    static ArchRule applicationLayerRule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("..application..")
        .should().dependOnClassesThat()
        .resideInAnyPackage("..infrastructure..", "..interfaces..");

    @ArchTest
    static ArchRule interfaceLayerRule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("..interfaces..")
        .should().dependOnClassesThat()
        .resideInAnyPackage("..infrastructure..");

    @ArchTest
    static ArchRule implementationPackageRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Impl")
        .should().resideInAPackage("..impl");

    @ArchTest
    static ArchRule interfacePackageRule = ArchRuleDefinition.classes()
        .that().areInterfaces()
        .should().resideOutsideOfPackage("..impl");

    @ArchTest
    static ArchRule controllerPackageRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(RestController.class)
        .should().resideInAPackage("..interfaces..");

    @ArchTest
    static ArchRule entityPackageRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(Entity.class)
        .should().resideInAPackage("..domain..");

}
