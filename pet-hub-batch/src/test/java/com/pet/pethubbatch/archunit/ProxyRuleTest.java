package com.pet.pethubbatch.archunit;

import com.pet.pethubbatch.PetHubBatchApplication;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.library.ProxyRules.no_classes_should_directly_call_other_methods_declared_in_the_same_class_that_are_annotated_with;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = PetHubBatchApplication.class)
class ProxyRuleTest {

    @ArchTest
    public static final ArchRule publicTransactionalMethodRule = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(Transactional.class)
        .should().bePublic();

    @ArchTest
    public static final ArchRule useSpringTransactionalRule = ArchRuleDefinition.noMethods()
        .should().beAnnotatedWith(jakarta.transaction.Transactional.class);

    @ArchTest
    public static final ArchRule invokeTransactionalMethodRule = no_classes_should_directly_call_other_methods_declared_in_the_same_class_that_are_annotated_with(
        Transactional.class);

}
