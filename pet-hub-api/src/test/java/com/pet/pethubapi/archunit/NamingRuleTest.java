package com.pet.pethubapi.archunit;

import com.pet.pethubapi.PetHubApiApplication;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = PetHubApiApplication.class)
public class NamingRuleTest {

    @ArchTest
    static ArchRule dtoNamingRule = ArchRuleDefinition.noClasses()
        .should().haveSimpleNameEndingWith("Dto"); // suffix should be in uppercase

    @ArchTest
    static ArchRule entityNamingRule = ArchRuleDefinition.noClasses()
        .that().areAnnotatedWith(Entity.class)
        .should().haveSimpleNameEndingWith("Entity");

    @ArchTest
    static ArchRule controllerNamingRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(RestController.class)
        .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static ArchRule repositoryNamingRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(Repository.class)
        .should().haveSimpleNameEndingWith("Repository");

    @ArchTest
    static ArchRule serviceNamingRule = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(Service.class)
        .should().haveSimpleNameEndingWith("ServiceImpl");

    @ArchTest
    static ArchRule interfaceNamingRule = ArchRuleDefinition.noClasses()
        .that().areInterfaces()
        .should().haveSimpleNameEndingWith("Interface").orShould().haveSimpleNameStartingWith("I");

    @ArchTest
    static ArchRule testClassNamingRule = ArchRuleDefinition.classes()
        .that().containAnyMethodsThat(annotatedWith(Test.class))
        .should().haveSimpleNameEndingWith("Should");

}
