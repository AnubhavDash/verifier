/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block1.verifications;

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
public abstract class Block1VerificationAbstractTest {

	protected AbstractVerification verification;
	@Autowired
	ApplicationContext context;
	Class<? extends AbstractVerification> verificationClass;

	public Block1VerificationAbstractTest(Class<? extends AbstractVerification> verificationClass) {
		this.verificationClass = verificationClass;
	}

	@PostConstruct
	private void setup() {
		verification = context.getAutowireCapableBeanFactory().createBean(verificationClass);
	}

	@Test
	void verificationDefinitionTestOK() {
		// Check that @PostConstruct method is correctly implemented in each sub test class
		assertNotNull(verification);

		VerificationDefinition verificationDefinition = verification.getVerificationDefinition();
		// Minimum required definition
		assertNotNull(verificationDefinition);
		assertEquals(1, verificationDefinition.getBlockId());
		assertNotNull(verificationDefinition.getCategory());
		assertNotNull(verificationDefinition.getName());
		assertNotNull(verificationDefinition.getDescription());
		// Check verification is not deactivated
		assertFalse(verificationDefinition.isDeactivated(), "The verification must not be deactivated");
	}

}
