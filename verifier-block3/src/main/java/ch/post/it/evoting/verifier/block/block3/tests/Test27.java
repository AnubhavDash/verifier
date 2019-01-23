package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.decrypt.DecryptVerifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test27 extends Test {
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
            File[] ccMixingKeys = PathHelper.getFiles(inputDirectory.toPath().resolve(Block3TestSuite.PATH_CC_MIXING_KEYS).toFile(), "cc.*_mixing_public_key.json");

            for (File ballotBox : ballotBoxes) {
                final File[] onlineMixings = ballotBox.listFiles(((dir, name) -> name.matches(".*ccn_m.?\\.json")));
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
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof TestFailureException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test07.nok.message", ((TestFailureException) e).getArgs()[1]));
            } else if (e instanceof RuntimeException) {
                if (e.getCause() instanceof FileNotFoundException) {
                    result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test07.file.not.found.message", e.getCause().getLocalizedMessage()));
                }
            }
        }
        return result;
    }

    private File getPkJsonFile(String name, File[] ccMixingKeys) {
        File[] result = new File[1];
        Pattern pattern = Pattern.compile(".*ccn_m(.?)\\.json");
        Matcher matcher = pattern.matcher(name);
        matcher.matches();
        String id = matcher.group(1);
        Arrays.stream(ccMixingKeys).forEach( file -> {
                    if (file.getName().contains(id)){
                        result[0] = file;
                    }
                });
        return result[0];
    }
}
