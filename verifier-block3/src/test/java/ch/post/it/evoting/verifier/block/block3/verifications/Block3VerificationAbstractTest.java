package ch.post.it.evoting.verifier.block.block3.verifications;

import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import org.junit.Assert;
import org.junit.Test;

public abstract class Block3VerificationAbstractTest {
    protected AbstractVerification verification;

    @Test
    public void verificationDefinitionTestOK(){
        // Check that @Before method is correctly implemented in each sub test class
        Assert.assertNotNull(verification);

        VerificationDefinition verificationDefinition = verification.getVerificationDefinition();
        // Minimum required definition
        Assert.assertNotNull(verificationDefinition);
        Assert.assertNotNull(verificationDefinition.getCategory());
        Assert.assertNotNull(verificationDefinition.getName());
        Assert.assertNotNull(verificationDefinition.getDescription());
        // Check verification is not deactivated
        Assert.assertFalse("The verification must not be deactivated", verificationDefinition.isDeactivated());
    }
}
