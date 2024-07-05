package com.pet.pethubapi.archunit;

import com.pet.pethubapi.PetHubApiApplication;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.library.ProxyRules.no_classes_should_directly_call_other_methods_declared_in_the_same_class_that_are_annotated_with;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = PetHubApiApplication.class)
public class ProxyRuleTest {

    @ArchTest
    static ArchRule publicTransactionalMethodRule = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(Transactional.class)
        .should().bePublic();

    @ArchTest
    static ArchRule useSpringTransactionalRule = ArchRuleDefinition.noMethods()
        .should().beAnnotatedWith(jakarta.transaction.Transactional.class);

    @ArchTest
    static ArchRule invokeTransactionalMethodRule = no_classes_should_directly_call_other_methods_declared_in_the_same_class_that_are_annotated_with(
        Transactional.class);

}
