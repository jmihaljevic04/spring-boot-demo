package com.pet.pethubsecurity.archunit;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@SuppressWarnings("unused")
@AnalyzeClasses(packages = "com.pet.pethubsecurity")
class AnnotationRuleTest {

    @ArchTest
    public static final ArchRule serviceRepositoryAnnotationRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Impl")
        .should().beAnnotatedWith(Service.class).orShould().beAnnotatedWith(Repository.class);

    @ArchTest
    public static final ArchRule repositoryAnnotationRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Repository")
        .should().beAnnotatedWith(Repository.class);

}
