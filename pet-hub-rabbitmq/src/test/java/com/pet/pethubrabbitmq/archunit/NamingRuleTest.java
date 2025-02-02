package com.pet.pethubrabbitmq.archunit;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;

@SuppressWarnings("unused")
@AnalyzeClasses(packages = "com.pet.pethubrabbitmq")
class NamingRuleTest {

    @ArchTest
    public static final ArchRule dtoNamingRule = ArchRuleDefinition.noClasses()
        .should().haveSimpleNameEndingWith("Dto"); // suffix should be in uppercase

    @ArchTest
    public static final ArchRule interfaceNamingRule = ArchRuleDefinition.noClasses()
        .that().areInterfaces()
        .should().haveSimpleNameEndingWith("Interface").orShould().haveSimpleNameStartingWith("I");

    @ArchTest
    public static final ArchRule testClassNamingRule = ArchRuleDefinition.classes()
        .that().containAnyMethodsThat(annotatedWith(Test.class))
        .should().haveSimpleNameEndingWith("Should");

    @ArchTest
    public static final ArchRule utilityClassNamingRule = ArchRuleDefinition.noClasses()
        .should().haveSimpleNameEndingWith("Util"); // use *Utils

    @ArchTest
    public static final ArchRule noHelperClassNamingRule = ArchRuleDefinition.noClasses()
        .should().haveSimpleNameContaining("Helper"); // use *Utils

    @ArchTest
    public static final ArchRule enumClassNamingRule = ArchRuleDefinition.classes()
        .that().areEnums()
        .should().haveSimpleNameEndingWith("Enum")
        .allowEmptyShould(true);

}
