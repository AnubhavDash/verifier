/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block1.diabledTests;

import ch.post.it.evoting.verifier.block.block1.Block1TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test07 of Block1, Step checkCommitmentParameters(cp)
 */
public class Test08 extends Test {

    private static final Logger log = Logger.getLogger(Test08.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test08.description"));
        def.setId(8);
        def.setName("checkCommitmentParameters(cp)");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            Path path = inputDirectory.toPath().resolve(Block1TestSuite.PATH_CRYPTO_SETUP);
            FileInputStream fis = new FileInputStream(new File(path.toFile() + "/commitmentParameters.txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            List<BigInteger> numbers = new ArrayList<>();
            String line = null;
            while ((line = br.readLine()) != null) {
                numbers.add(new BigInteger(line));
            }
            br.close();

            if(numbers.isEmpty()){
                throw new Exception("No such numbers was found in commitmentParameters file");
            }
            else{
                BigInteger p = numbers.get(0);
                BigInteger q = numbers.get(1);
                BigInteger g = numbers.get(2);
                numbers = numbers.subList(3, numbers.size());

                List<BigInteger> errors = numbers.stream()
                        .filter(bigInteger -> !MathHelper.isEulerCriterionValid(bigInteger, p))
                        .collect(Collectors.toList());
                if (errors.isEmpty()) {
                    result.setStatus(Status.OK);
                } else {
                    result.setStatus(Status.NOK);
                    result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test08.nok.message", errors.toString()));
                }
            }
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if(e instanceof FileNotFoundException){
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test08.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }
}
