/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block1.tests;

import ch.post.it.evoting.verifier.block.block1.Block1TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.dto.ConfigurationType;
import ch.post.it.evoting.verifier.common.block.tools.LanguageHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeHelper;
import ch.post.it.evoting.verifier.common.block.tools.XMLMapper;
import com.sun.xml.internal.bind.v2.TODO;
import org.apache.log4j.Logger;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test09 of Block1, Step checkPrimeNumberOptions([vo])
 */
public class Test09 extends Test {

    private static final Logger log = Logger.getLogger(Test09.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test09.description"));
        def.setId(9);
        def.setName("checkPrimeNumberOptions([vo])");
        return def;
    }

    // common method used to verify if a vo is in error
    // if Euler criterion is not equals to 1 there is an error
    private boolean isBigIntInError(BigInteger vo, BigInteger p){
        boolean inError = false;
        BigInteger exponent = (p.subtract(BigInteger.ONE)).divide(new BigInteger("2"));
        BigInteger ec = vo.modPow(exponent, p);
        inError = !ec.equals(BigInteger.ONE);
        return inError;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            ConfigurationType configuration = XMLMapper.mapFromXml(inputDirectory, "configuration-anonymized.xml", ConfigurationType.class);

            // TODO
            /*
            Read the file configuration-anonymized.xml
- For each vote, read the voteIdentification. Count the number of <answer> elements (Example. The vote
has one variantBallot with two standardQuestions and one tie-break question, each having three possible
answers. Therefore, the vote has 9 answer elements in total).
- For each election: Read the fields: numberOfMandates, writeInsAllowed and candidateAccumulation
o Count the number of <candidate> elements. Count the number of candidate options as follows
𝑐𝑎𝑛𝑑𝑖𝑑𝑎𝑡𝑒𝑉𝑜𝑡𝑖𝑛𝑔𝑂𝑝𝑡𝑖𝑜𝑛𝑠 = (#𝑐𝑎𝑛𝑑𝑖𝑑𝑎𝑡𝑒𝑠 ∗ 𝑐𝑎𝑛𝑑𝑖𝑑𝑎𝑡𝑒𝐴𝑐𝑐𝑢𝑚𝑢𝑙𝑎𝑡𝑖𝑜𝑛) + 𝑛𝑢𝑚𝑏𝑒𝑟𝑂𝑓𝑀𝑎𝑛𝑑𝑎𝑡𝑒𝑠 ∗ (1 + 𝑤𝑟𝑖𝑡𝑒𝐼𝑛𝑠𝐴𝑙𝑙𝑜𝑤𝑒𝑑)
Example: 7 mandates, 10 candidates, candidateAccumulation = 2, writeInsAllowed = true.
𝑐𝑎𝑛𝑑𝑖𝑑𝑎𝑡𝑒𝑉𝑜𝑡𝑖𝑛𝑔𝑂𝑝𝑡𝑖𝑜𝑛𝑠 = (10 ∗ 2) + 7 (1 + 1) = 34
o Count the number of <list> elements.
             */

            String pString = "1a";
            BigInteger p = TypeHelper.base64ToBigInteger(pString);

            FileInputStream fis = new FileInputStream(new File(inputDirectory + "/commitmentParameters.txt"));
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
                List<BigInteger> errors = numbers.stream()
                        .filter(bigInteger -> isBigIntInError(bigInteger, p))
                        .collect(Collectors.toList());
                if (errors.isEmpty()) {
                    result.setStatus(Status.OK);
                } else {
                    result.setStatus(Status.NOK);
                    result.setMessage(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test09.nok.message", errors.toString()));
                }
            }
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if(e instanceof FileNotFoundException){
                result.setMessage(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test09.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }
}
