package com.pet.pethubapi.archunit;

import com.pet.pethubapi.PetHubApiApplication;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = PetHubApiApplication.class)
public class AnnotationRuleTest {

    @ArchTest
    static ArchRule serviceAnnotationRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("ServiceImpl")
        .should().beAnnotatedWith(Service.class);

    @ArchTest
    static ArchRule repositoryAnnotationRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Repository")
        .should().beAnnotatedWith(Repository.class);

    @ArchTest
    static ArchRule restControllerAnnotationRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Controller")
        .should().beAnnotatedWith(RestController.class);

    @ArchTest
    static ArchRule controllerAnnotationRule = ArchRuleDefinition.noClasses()
        .should().beAnnotatedWith(Controller.class);

    @ArchTest
    static ArchRule junit4TestAnnotationRule = ArchRuleDefinition.noMethods()
        .should().beAnnotatedWith(org.junit.Test.class);

}
