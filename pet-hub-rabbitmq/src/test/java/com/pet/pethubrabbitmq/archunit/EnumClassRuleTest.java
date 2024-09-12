package com.pet.pethubrabbitmq.archunit;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

@SuppressWarnings("unused")
@AnalyzeClasses(packages = "com.pet.pethubrabbitmq")
public class EnumClassRuleTest {

    @ArchTest
    public static final ArchRule privateFinalFieldsRule = ArchRuleDefinition.fields()
        .that().areDeclaredInClassesThat().areEnums().and().areNotStatic()
        .should().bePrivate().andShould().beFinal()
        .allowEmptyShould(true);

}