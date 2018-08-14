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
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.ElectoralAuthority;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Test07 of Block1, Step isMemberOfGroup(pk_ea)
 */
public class Test07 extends Test {

    private static final Logger log = Logger.getLogger(Test07.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test07.description"));
        def.setId(7);
        def.setName("isMemberOfGroup(pk_ea)");
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

            ElectoralAuthority electoralAuthority = Deserializer.fromJson(inputDirectory, "electoralAuthority\\.json", ElectoralAuthority.class);
            String publicKeyB64 = electoralAuthority.getPublicKey();
            byte[] decoded = TypeConverter.base64ToByte(publicKeyB64);
            String publicKey = TypeConverter.byteToString(decoded);

            BigInteger p = extractPFromPublicKey(publicKey);
            List<String> elements = extractElementsFromPublicKey(publicKey);
            if(elements.isEmpty()){
                throw new Exception("No such Elements was found in the publicKey");
            }
            else{
                List<String> errors = elements.stream()
                                                .map(element -> TypeConverter.byteToBigInteger(TypeConverter.base64ToByte(element)))
                                                .filter(bigInteger -> isBigIntInError(bigInteger, p))
                                                .map(bi -> TypeConverter.byteToBase64String(TypeConverter.bigIntegerToByte(bi)))
                                                .collect(Collectors.toList());
                if (errors.isEmpty()) {
                    result.setStatus(Status.OK);
                } else {
                    result.setStatus(Status.NOK);
                    result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test07.nok.message", errors.toString()));
                }
            }
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if(e instanceof FileNotFoundException){
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test07.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }

    private BigInteger extractPFromPublicKey(String publicKey) {
        List<BigInteger> result = new ArrayList<>();
        if(publicKey != null && !publicKey.isEmpty() && publicKey.contains("p")){
            String[] split = publicKey.split("\"");
            int indexOf = Arrays.asList(split).indexOf("p");

            result.addAll(IntStream
                    .range(0, split.length)
                    .filter(i -> (i == indexOf + 2))
                    .mapToObj(i -> TypeConverter.byteToBigInteger(TypeConverter.base64ToByte(split[i])))
                    .collect(Collectors.toList()));
        }
        return result.get(0);
    }

    private List<String> extractElementsFromPublicKey(String publicKey) {
        List<String> result = new ArrayList<>();
        if(publicKey != null && !publicKey.isEmpty() && publicKey.contains("elements")){
            String[] split = publicKey.split("\"");
            int indexOf = Arrays.asList(split).indexOf("elements");

            result.addAll(IntStream
                    .range(0, split.length)
                    .filter(i -> (i > indexOf && i % 2 == 1 ))
                    .mapToObj(i -> split[i])
                    .collect(Collectors.toList()));
        }
        return result;
    }
}
