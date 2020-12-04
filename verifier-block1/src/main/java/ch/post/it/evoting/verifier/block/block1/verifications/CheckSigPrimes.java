package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

import java.nio.file.Files;
import java.nio.file.Path;

public class CheckSigPrimes extends AbstractVerification {
    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                "verification82.description"));
        def.setId(82);
        def.setName("checkSigPrimes");
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        // Get the file paths.
        final PathNode primesPathNode = pathService.buildFromRootPath(StructureKey.PRIMES, inputDirectoryPath);
        final Path sigPath = primesPathNode.getRelation(RelationType.P7);
        final PathNode rootCertificatePathNode = pathService.buildFromRootPath(StructureKey.INTEGRATION_CA, inputDirectoryPath);

        byte[] source = Files.readAllBytes(primesPathNode.getPath());
        byte[] signature = Files.readAllBytes(sigPath);
        byte[] rootCertificate = Files.readAllBytes(rootCertificatePathNode.getPath());

        // Verify signature.
        if (!SignatureChecker.verifyPKCS7(source, signature, rootCertificate)) {
            throw buildVerificationFailureException(
                    "The signature verification of the file failed.",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification82.nok.message",
                    primesPathNode.toString()
            );
        }

        result.setStatus(Status.OK);
        return result;
    }
}
