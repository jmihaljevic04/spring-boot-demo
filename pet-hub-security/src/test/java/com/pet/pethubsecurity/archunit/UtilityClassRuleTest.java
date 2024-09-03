package com.pet.pethubsecurity.archunit;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

@SuppressWarnings("unused")
@AnalyzeClasses(packages = "com.pet.pethubsecurity")
public class UtilityClassRuleTest {

    @ArchTest
    public static final ArchRule privateConstructorRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Utils").or().resideInAPackage("..util")
        .should().haveOnlyPrivateConstructors()
        .allowEmptyShould(true);

    @ArchTest
    public static final ArchRule finalFieldsRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Utils").or().resideInAPackage("..util")
        .should().haveOnlyFinalFields()
        .allowEmptyShould(true);

    @ArchTest
    public static final ArchRule staticMethodRule = ArchRuleDefinition.methods()
        .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Utils").or().areDeclaredInClassesThat().resideInAPackage("..util")
        .should().beStatic()
        .allowEmptyShould(true);

}
