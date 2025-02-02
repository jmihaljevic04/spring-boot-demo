package com.pet.pethubapi.archunit;

import com.pet.pethubapi.PetHubApiApplication;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = PetHubApiApplication.class)
class NamingRuleTest {

    @ArchTest
    public static final ArchRule dtoNamingRule = ArchRuleDefinition.noClasses()
        .should().haveSimpleNameEndingWith("Dto"); // suffix should be in uppercase

    @ArchTest
    public static final ArchRule entityNamingRule = ArchRuleDefinition.noClasses()
        .that().areAnnotatedWith(Entity.class)
        .should().haveSimpleNameEndingWith("Entity");

    @ArchTest
    public static final ArchRule controllerNamingRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(RestController.class)
        .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    public static final ArchRule repositoryNamingRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(Repository.class)
        .should().haveSimpleNameEndingWith("Repository");

    @ArchTest
    public static final ArchRule interfaceNamingRule = ArchRuleDefinition.noClasses()
        .that().areInterfaces()
        .should().haveSimpleNameEndingWith("Interface").orShould().haveSimpleNameStartingWith("I");

    @ArchTest
    public static final ArchRule testClassNamingRule = ArchRuleDefinition.classes()
        .that().containAnyMethodsThat(annotatedWith(Test.class))
        .should().haveSimpleNameEndingWith("Should");

    @ArchTest
    public static final ArchRule utilityClassNamingRule = ArchRuleDefinition.noClasses()
        .should().haveSimpleNameEndingWith("Util"); // use *Utils

    @ArchTest
    public static final ArchRule noHelperClassNamingRule = ArchRuleDefinition.noClasses()
        .should().haveSimpleNameContaining("Helper"); // use *Utils

    @ArchTest
    public static final ArchRule enumClassNamingRule = ArchRuleDefinition.classes()
        .that().areEnums()
        .should().haveSimpleNameEndingWith("Enum");

}
