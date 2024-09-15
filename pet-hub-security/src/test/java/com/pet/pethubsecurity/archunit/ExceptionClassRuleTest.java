package com.pet.pethubsecurity.archunit;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

@SuppressWarnings("unused")
@AnalyzeClasses(packages = "com.pet.pethubsecurity")
class ExceptionClassRuleTest {

    @ArchTest
    public static final ArchRule extendRuntimeExRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Exception")
        .should().beAssignableTo(RuntimeException.class);

}
