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
    public static final ArchRule domainLayerRule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat()
        .resideInAnyPackage("..application..", "..infrastructure..", "..interfaces..");

    @ArchTest
    public static final ArchRule applicationLayerRule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("..application..")
        .should().dependOnClassesThat()
        .resideInAnyPackage("..interfaces..");

    @ArchTest
    public static final ArchRule interfaceLayerRule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("..interfaces..")
        .should().dependOnClassesThat()
        .resideInAnyPackage("..domain..");

    @ArchTest
    public static final ArchRule infrastructureLayerRule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("..infrastructure..")
        .should().dependOnClassesThat()
        .resideInAnyPackage("..interfaces..", "..application..");

    @ArchTest
    public static final ArchRule packageStructureRule = ArchRuleDefinition.classes()
        .that().doNotHaveSimpleName("PetHubApiApplication").and().doNotHaveSimpleName("PetHubApiApplicationShould").and().doNotHaveSimpleName(
            "TestcontainersConfiguration").and().resideOutsideOfPackage("..archunit..")
        .should().resideInAnyPackage("..application..", "..infrastructure..", "..interfaces..", "..domain..");

    @ArchTest
    public static final ArchRule implementationPackageRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Impl")
        .should().resideInAPackage("..impl");

    @ArchTest
    public static final ArchRule interfacePackageRule = ArchRuleDefinition.classes()
        .that().areInterfaces()
        .should().resideOutsideOfPackage("..impl");

    @ArchTest
    public static final ArchRule controllerPackageRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(RestController.class)
        .should().resideInAPackage("..interfaces..");

    @ArchTest
    public static final ArchRule entityPackageRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(Entity.class)
        .should().resideInAPackage("..domain..");

}
