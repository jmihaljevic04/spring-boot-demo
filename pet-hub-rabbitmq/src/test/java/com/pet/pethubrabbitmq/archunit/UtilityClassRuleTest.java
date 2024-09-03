package com.pet.pethubrabbitmq.archunit;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

@SuppressWarnings("unused")
@AnalyzeClasses(packages = "com.pet.pethubrabbitmq")
public class UtilityClassRuleTest {

    @ArchTest
    public static final ArchRule privateConstructorRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Utils").or().resideInAPackage("..util")
        .should().haveOnlyPrivateConstructors();

    @ArchTest
    public static final ArchRule finalFieldsRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Utils").or().resideInAPackage("..util")
        .should().haveOnlyFinalFields();

    @ArchTest
    public static final ArchRule staticMethodRule = ArchRuleDefinition.methods()
        .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Utils").or().areDeclaredInClassesThat().resideInAPackage("..util")
        .should().beStatic();

}
