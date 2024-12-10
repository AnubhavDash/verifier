/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.verifier.backend;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.freeze.FreezingArchRule;

@AnalyzeClasses(packages = "ch.post.it.evoting.verifier.backend")
class ArchitectureTest {

	@ArchTest
	static final ArchRule controllersAreWellAnnotated = classes().that()
			.haveNameMatching(".*Controller")
			.should()
			.beAnnotatedWith(RestController.class)
			.orShould()
			.beAnnotatedWith(Controller.class);

	@ArchTest
	static final ArchRule classesThatAreNotControllersAreWellAnnotated = classes().that()
			.haveNameNotMatching(".*Controller")
			.should()
			.notBeAnnotatedWith(RestController.class)
			.andShould()
			.notBeAnnotatedWith(Controller.class);

	@ArchTest
	static final ArchRule servicesAreWellAnnotated = classes().that()
			.haveNameMatching(".*(Service|Algorithm)")
			.should()
			.beAnnotatedWith(Service.class);

	@ArchTest
	static final ArchRule classesThatAreNotServicesAreWellAnnotated = classes().that()
			.haveNameNotMatching(".*(Service|Algorithm)")
			.should()
			.notBeAnnotatedWith(Service.class);

	@ArchTest
	static final ArchRule noCyclesInBackend = slices()
			.matching("..(backend).(*)..")
			.namingSlices("$2 of $1")
			.should()
			.beFreeOfCycles();

	@ArchTest
	static final ArchRule noCyclesInSetup = slices()
			.matching("..(setup).(*)..")
			.namingSlices("$2 of $1")
			.should()
			.beFreeOfCycles();

	@ArchTest
	static final ArchRule noCyclesInTally = slices()
			.matching("..(tally).(*)..")
			.namingSlices("$2 of $1")
			.should()
			.beFreeOfCycles();

	@ArchTest
	static final ArchRule noDependenciesFromSetupPackage = noClasses().that()
			.resideInAPackage("ch.post.it.evoting.verifier.backend.verifications.setup..")
			.should().dependOnClassesThat()
			.resideInAnyPackage("ch.post.it.evoting.verifier.backend.verifications.tally..");

	@ArchTest
	static final ArchRule noDependenciesFromTallyPackage = noClasses().that()
			.resideInAPackage("ch.post.it.evoting.verifier.backend.verifications.tally..")
			.should().dependOnClassesThat()
			.resideInAnyPackage("ch.post.it.evoting.verifier.backend.verifications.setup..");
}
