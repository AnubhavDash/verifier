/*
 * (c) Copyright 2025 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.backend;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "ch.post.it.evoting.verifier.backend")
class ArchitectureTest {

	@ArchTest
	static final ArchRule noClassesShouldCallLocalDateNow = noClasses().should().callMethod(LocalDate.class, "now");

	@ArchTest
	static final ArchRule noClassesShouldCallLocalDateTimeNow = noClasses().should().callMethod(LocalDateTime.class, "now");

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
