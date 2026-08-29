package br.voy.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Architecture Fitness Functions")
class ArchitectureFitnessTest {

    private static JavaClasses classes;

    private static final DescribedPredicate<JavaClass> DOMAIN_TO_INFRA_DEBT =
            new DescribedPredicate<JavaClass>(
                    "known tech debt — PlaceRepository uses PlaceModel parameter") {
                @Override
                public boolean test(JavaClass c) {
                    return c.getSimpleName().equals("PlaceRepository");
                }
            };

    private static final DescribedPredicate<JavaClass> NOT_DOMAIN_TO_INFRA_DEBT =
            new DescribedPredicate<JavaClass>("not known tech debt (PlaceRepository)") {
                @Override
                public boolean test(JavaClass c) {
                    return !c.getSimpleName().equals("PlaceRepository");
                }
            };

    @BeforeAll
    static void importClasses() {
        classes =
                new ClassFileImporter()
                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                        .importPackages("br.voy");
    }

    @Test
    @DisplayName("Domain must not depend on Infrastructure layer")
    void domainMustNotDependOnInfrastructure() {
        noClasses()
                .that()
                .resideInAPackage("br.voy.domain..")
                .and(NOT_DOMAIN_TO_INFRA_DEBT)
                .should()
                .dependOnClassesThat()
                .resideInAPackage("br.voy.infrastructure..")
                .check(classes);
    }

    @Test
    @DisplayName("Domain must not depend on Application layer")
    void domainMustNotDependOnApplication() {
        noClasses()
                .that()
                .resideInAPackage("br.voy.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("br.voy.application..")
                .check(classes);
    }

    @Test
    @DisplayName("Application must not depend on Infrastructure layer")
    void applicationMustNotDependOnInfrastructure() {
        noClasses()
                .that()
                .resideInAPackage("br.voy.application..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("br.voy.infrastructure..")
                .check(classes);
    }

    @Test
    @DisplayName("Domain must not import JPA annotations (javax.persistence)")
    void domainMustNotUseJpaAnnotations() {
        noClasses()
                .that()
                .resideInAPackage("br.voy.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("javax.persistence..")
                .check(classes);
    }

    @Test
    @DisplayName("Controllers must reside in application.controller package")
    void controllersMustResideInApplicationLayer() {
        classes()
                .that()
                .haveSimpleNameEndingWith("Controller")
                .should()
                .resideInAPackage("br.voy.application.controller..")
                .check(classes);
    }

    @Test
    @DisplayName("Use cases (interfaces) must reside in domain package")
    void useCasesMustResideInDomainLayer() {
        classes()
                .that()
                .haveSimpleNameEndingWith("UseCase")
                .should()
                .resideInAPackage("br.voy.domain..")
                .check(classes);
    }

    @Test
    @DisplayName("Domain exceptions must reside in domain.exception package")
    void exceptionsMustResideInDomainExceptionPackage() {
        classes()
                .that()
                .haveSimpleNameEndingWith("Exception")
                .and()
                .resideInAPackage("br.voy..")
                .should()
                .resideInAPackage("br.voy.domain.exception..")
                .check(classes);
    }

    @Test
    @DisplayName("JPA models must reside in infrastructure.model package")
    void jpaModelsMustResideInInfrastructureLayer() {
        classes()
                .that()
                .haveSimpleNameEndingWith("Model")
                .and()
                .resideInAPackage("br.voy..")
                .should()
                .resideInAPackage("br.voy.infrastructure.model..")
                .check(classes);
    }
}
