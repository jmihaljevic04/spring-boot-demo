package com.pet.pethubapi.archunit;

import com.pet.pethubapi.PetHubApiApplication;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

@SuppressWarnings("unused")
@AnalyzeClasses(packagesOf = PetHubApiApplication.class)
public class UtilityClassRuleTest {

    @ArchTest
    public static final ArchRule privateConstructorRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Utils")
        .should().haveOnlyPrivateConstructors();

    @ArchTest
    public static final ArchRule finalFieldsRule = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Utils")
        .should().haveOnlyFinalFields();

    @ArchTest
    public static final ArchRule staticMethodRule = ArchRuleDefinition.methods()
        .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Utils")
        .should().beStatic();

}
