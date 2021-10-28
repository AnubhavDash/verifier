/*
 * Copyright 2021 Post CH Ltd
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

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.test.SpringTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringTestConfiguration.class)
public abstract class Block3VerificationAbstractTest {

	protected AbstractVerification verification;
	@Autowired
	ApplicationContext context;
	Class<? extends AbstractVerification> verificationClass;

	public Block3VerificationAbstractTest(Class<? extends AbstractVerification> verificationClass) {
		this.verificationClass = verificationClass;
	}

	@PostConstruct
	private void setup() {
		verification = context.getAutowireCapableBeanFactory().createBean(verificationClass);
	}

	@Test
	void verificationDefinitionTestOK() {
		// Check that @BeforeEach method is correctly implemented in each sub test class
		assertNotNull(verification);

		VerificationDefinition verificationDefinition = verification.getVerificationDefinition();
		// Minimum required definition
		assertNotNull(verificationDefinition);
		assertEquals(3, verificationDefinition.getBlockId());
		assertNotNull(verificationDefinition.getCategory());
		assertNotNull(verificationDefinition.getName());
		assertNotNull(verificationDefinition.getDescription());
		// Check verification is not deactivated
		assertFalse(verificationDefinition.isDeactivated(), "The verification must not be deactivated");
	}
}
