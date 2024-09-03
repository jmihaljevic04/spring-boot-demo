package com.pet.pethubbatch.archunit;

import com.pet.pethubbatch.PetHubBatchApplication;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = PetHubBatchApplication.class)
public class AnnotationRuleTest {

    @ArchTest
    public static final ArchRule serviceRepositoryAnnotationRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Impl")
        .should().beAnnotatedWith(Service.class).orShould().beAnnotatedWith(Repository.class);

    @ArchTest
    public static final ArchRule repositoryAnnotationRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Repository")
        .should().beAnnotatedWith(Repository.class);

    @ArchTest
    public static final ArchRule restControllerAnnotationRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Controller")
        .should().beAnnotatedWith(RestController.class);

    @ArchTest
    public static final ArchRule controllerAnnotationRule = ArchRuleDefinition.noClasses()
        .should().beAnnotatedWith(Controller.class);

    @ArchTest
    public static final ArchRule junit4TestAnnotationRule = ArchRuleDefinition.noMethods()
        .should().beAnnotatedWith(org.junit.Test.class);

    @ArchTest
    public static final ArchRule jakartaTransactionalAnnotationRule = ArchRuleDefinition.noMethods()
        .should().beAnnotatedWith(jakarta.transaction.Transactional.class); // use Spring framework annotation

}
