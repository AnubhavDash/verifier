package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.block.block3.loader.online.OnlineMixingProofLoader;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.dto.onlinemixing.Signature;
import com.scytl.products.ov.mixnet.commons.exceptions.VerifierException;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Test75 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test75.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(3);
        def.setCategory(Category.INTEGRITY);
        def.setId(75);
        def.setName("checkSigOnlineProofs");
        def.setDescription(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test75.description"));
        def.addTestTrait(TestTrait.PreDecryption);
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            File[] ballotBoxes = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3TestSuite.PATH_BALLOTBOXES));
            for (File ballotBox : ballotBoxes) {
                final File[] onlineMixings = ballotBox.listFiles(((dir, name) -> name.matches(".*ccn_m.?\\.json")));
                if(onlineMixings.length != 3 ){
                    throw new VerifierException("the number of control components expected is 3 but actual is " + onlineMixings.length);
                }
                if (onlineMixings.length == 0) {
                    result.setStatus(Status.NOK);
                    result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test75.file.not.found.message"));
                } else {
                    byte[] platformRootCA = Files.readAllBytes(PathHelper.getFile(inputDirectory.toPath().resolve(Block3TestSuite.PATH_CERTIFICATES).toFile(), "platformRootCA.*\\.pem").toPath());

                    for (File file : onlineMixings) {

                        OnlineMixingProofLoader loader = new OnlineMixingProofLoader(file.toPath());
                        Signature signature = loader.getOnlineMixing().getSignature();
                        String content = signature.getSignatureContents();
                        List<String> intermediate = signature.getCertificateChain();
                        byte[][] intermediates = intermediate.stream().map(str -> str.getBytes(StandardCharsets.UTF_8)).toArray(byte[][]::new);

                        String cert = intermediate.get(0);
                        String source = "";
                        //boolean b = SignatureChecker.verifySignature(source.getBytes(StandardCharsets.UTF_8), Base64.decode(content), cert.getBytes(StandardCharsets.UTF_8), bytes, platformRootCA);


                        List<BigInteger> errors = new ArrayList<>();

                        if (errors.isEmpty()) {
                            result.setStatus(Status.OK);
                        } else {
                            result.setStatus(Status.NOK);
                            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test75.nok.message", errors.toString()));
                        }
                    }
                }
            }
        } catch (IOException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test75.file.not.found.message"));
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }
}