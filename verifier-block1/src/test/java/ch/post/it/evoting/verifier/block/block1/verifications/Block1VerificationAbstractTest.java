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

import ch.post.it.evoting.verifier.block.block1.config.Block1TestConfiguration;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.regex.Pattern;

@ExtendWith(SpringExtension.class)
@Configuration
@ContextConfiguration(classes = {Block1TestConfiguration.class})
public abstract class Block1VerificationAbstractTest {
    protected AbstractVerification verification;

    @Test
    public void verificationDefinitionTestOK() {
        // Check that @BeforeEach method is correctly implemented in each sub test class
        Assert.assertNotNull(verification);

        VerificationDefinition verificationDefinition = verification.getVerificationDefinition();
        // Minimum required definition
        Assert.assertNotNull(verificationDefinition);
        Assert.assertEquals(1, verificationDefinition.getBlockId());
        Assert.assertNotNull(verificationDefinition.getCategory());
        Assert.assertNotNull(verificationDefinition.getName());
        Assert.assertNotNull(verificationDefinition.getDescription());
        // Check verification is not deactivated
        Assert.assertFalse("The verification must not be deactivated", verificationDefinition.isDeactivated());
    }

}
