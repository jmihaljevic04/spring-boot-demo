package com.pet.pethubbatch.archunit;

import com.pet.pethubbatch.PetHubBatchApplication;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import jakarta.persistence.Entity;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = PetHubBatchApplication.class)
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
        .that(DescribedPredicate.not(resideInAnyPackage("com.pet.pethubbatch", "com.pet.pethubbatch.archunit")))
        .should().resideInAnyPackage("..application..", "..infrastructure..", "..interfaces..", "..domain..");

    @ArchTest
    public static final ArchRule implementationPackageRule = ArchRuleDefinition.noClasses()
        .should().resideInAPackage("..impl");

    @ArchTest
    public static final ArchRule utilityPackageRule = ArchRuleDefinition.noClasses()
        .should().resideInAPackage("..utils");

    @ArchTest
    public static final ArchRule controllerPackageRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(RestController.class)
        .should().resideInAPackage("..interfaces..");

    @ArchTest
    public static final ArchRule entityPackageRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(Entity.class)
        .should().resideInAPackage("..domain..");

}
