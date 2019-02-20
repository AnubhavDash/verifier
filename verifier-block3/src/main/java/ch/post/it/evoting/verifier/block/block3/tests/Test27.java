package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.decrypt.DecryptVerifier;
import com.scytl.products.ov.mixnet.commons.exceptions.VerifierException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test27 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test27.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(3);
        def.setCategory(Category.COMPLETENESS);
        def.setId(27);
        def.setName("checkDecryptionProofOnline");
        def.setDescription(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test27.description"));
        def.addTestTrait(TestTrait.PreDecryption);
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            File[] ballotBoxes = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3TestSuite.PATH_BALLOTBOXES));
            File[] ccMixingKeys = PathHelper.getFiles(inputDirectory.toPath().resolve(Block3TestSuite.PATH_CC_MIXING_KEYS).toFile(), "cc.*_mixing_.*key.*\\.json");

            for (File ballotBox : ballotBoxes) {
                final File[] onlineMixings = ballotBox.listFiles(((dir, name) -> name.matches(".*ccn_m.?\\.json")));
                if(onlineMixings.length != 3 ){
                    throw new VerifierException("the number of control components expected is 3 but actual is " + onlineMixings.length);
                }
                for (File onlineMixing : onlineMixings) {
                    //for this onlineMixing, so for this ccn , get the correct ccX_mixing_public_key.json file
                    File pkJsonFile = getPkJsonFile(onlineMixing.getName(), ccMixingKeys);
                    int verificationResultCode = DecryptVerifier.verifyOnline(onlineMixing.toPath(), pkJsonFile);
                    if (verificationResultCode != 1 && verificationResultCode != -1) {
                        throw new TestFailureException("The verification failed", ballotBox.getName());
                    }
                }
            }
            result.setStatus(Status.OK);
        } catch (TestFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test07.nok.message", ((TestFailureException) e).getArgs()[1]));
        } catch (FileNotFoundException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test07.file.not.found.message", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }

    private File getPkJsonFile(String name, File[] ccMixingKeys) {
        Pattern pattern = Pattern.compile(".*ccn_m(.?)\\.json");
        Matcher matcher = pattern.matcher(name);
        matcher.matches();
        String id = matcher.group(1);
        return Arrays.stream(ccMixingKeys)
                .filter(file -> file.getName().contains(String.format("cc%s_mixing", id)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("PublicKey file not found"));
    }
}
