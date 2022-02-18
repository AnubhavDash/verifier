/*
 * Copyright 2022 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.block.block3.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import ch.post.it.evoting.verifier.plugin.contract.AbstractVerification;
import ch.post.it.evoting.verifier.plugin.contract.VerificationDefinition;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathService;

public abstract class Block3VerificationTest {

	protected static AbstractVerification verification;
	protected static PathService pathService;
	protected static ApplicationEventPublisher applicationEventPublisherMock;

	@BeforeAll
	static void baseSetUpAll() {
		pathService = new PathService();
		applicationEventPublisherMock = mock(ApplicationEventPublisher.class);
	}

	@BeforeEach
	void baseSetUp() {
		reset(applicationEventPublisherMock);
	}

	@Test
	void verificationDefinitionTestOK() {
		assertNotNull(verification);

		VerificationDefinition verificationDefinition = verification.getVerificationDefinition();
		// Minimum required definition
		assertNotNull(verificationDefinition);
		assertEquals(3, verificationDefinition.getBlockId());
		assertNotNull(verificationDefinition.getCategory());
		assertNotNull(verificationDefinition.getName());
		assertNotNull(verificationDefinition.getDescription());
		assertFalse(verificationDefinition.getVerificationTraits().isEmpty());
	}
}
