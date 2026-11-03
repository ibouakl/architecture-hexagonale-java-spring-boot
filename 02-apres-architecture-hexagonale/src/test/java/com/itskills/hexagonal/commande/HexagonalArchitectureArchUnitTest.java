package com.itskills.hexagonal.commande;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * GARDIEN D'ARCHITECTURE AUTOMATISÉ (ArchUnit — Itération 3) :
 * Fait échouer le build Maven si un développeur importe Spring, JPA ou un Adaptateur dans le Domaine.
 */
@AnalyzeClasses(
        packages = "com.itskills.hexagonal.commande",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalArchitectureArchUnitTest {

    @ArchTest
    static final ArchRule le_domaine_ne_doit_jamais_dependre_de_l_infrastructure =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                    .because("La règle d'Inversion de Dépendance interdit au Domaine de connaître les Adaptateurs");

    @ArchTest
    static final ArchRule le_domaine_doit_rester_100_pourcent_java_pur_sans_spring_ni_jpa =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.springframework..",
                            "jakarta.persistence..",
                            "org.hibernate.."
                    )
                    .because("Le Cœur Métier doit être indépendant de tout framework technique");
}
