package ch.post.it.evoting.verifier.common.block.tools;

import ch.post.it.evoting.verifier.common.block.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class MathHelperTest {

    @Test
    public void modExpWithSimpleValues() {
        modExpTest("modExpSimpleValues.json");
    }

    @Test
    public void modExpWithRealSizeValues() {
        modExpTest("modExpRealSizeValues.json");
    }

    private void modExpTest(String jsonFileName) {
        List<ModExpParameters> modExpParametersList = readValues(jsonFileName, ModExpParameters[].class);
        modExpParametersList.forEach(modExpParameters -> {
            BigInteger output = MathHelper.modExp(modExpParameters.getB(), modExpParameters.getE(), modExpParameters.getM());
            assertEquals(modExpParameters.getId(), modExpParameters.getOutput(), output);
        });
    }


    @Test
    public void modExpProductWithSimpleValues() {
        modExpProductTest("modExpProductSimpleValues.json");
    }

    @Test
    public void modExpProductWithRealSizeValues() {
        modExpProductTest("modExpProductRealSizeValues.json");
    }

    private void modExpProductTest(String jsonFileName) {
        List<ModExpProductParameters> modExpProductParametersList =
                readValues(jsonFileName, ModExpProductParameters[].class);
        modExpProductParametersList.forEach(modExpProductParameters -> {
            BigInteger output = MathHelper.modExpProduct(
                    modExpProductParameters.getB_vec(), modExpProductParameters.getE_vec(), modExpProductParameters.getM());

            assertEquals(modExpProductParameters.getId(), modExpProductParameters.getOutput(), output);
        });
    }

    @Test
    public void modInvWithSimpleValues() {
        modInvTest("modInvSimpleValues.json");
    }

    @Test
    public void modInvWithRealSizeValues() {
        modInvTest("modInvRealSizeValues.json");
    }

    private void modInvTest(String jsonFileName) {
        List<ModInvParameters> modInvParametersList = readValues(jsonFileName, ModInvParameters[].class);
        modInvParametersList.forEach(modInvParameters -> {
            BigInteger output = MathHelper.modInv(modInvParameters.getB(), modInvParameters.getM());
            assertEquals(modInvParameters.getId(), modInvParameters.getOutput(), output);
        });
    }

    @Test
    public void computeCommitmentWithSimpleValues() {
        computeCommitmentTest("computeCommitmentSimpleValues.json");
    }

    @Test
    public void computeCommitmentWithRealSizeValues() {
        computeCommitmentTest("computeCommitmentRealSizeValues.json");
    }

    private void computeCommitmentTest(String jsonFileName) {
        List<ComputeCommitmentParameters> computeCommitmentParametersList =
                readValues(jsonFileName, ComputeCommitmentParameters[].class);
        computeCommitmentParametersList.forEach(computeCommitmentParameters -> {
            BigInteger output = MathHelper.computeCommitment(
                    computeCommitmentParameters.getEg(),
                    computeCommitmentParameters.getR(),
                    computeCommitmentParameters.getA_vec(),
                    computeCommitmentParameters.getCk()
            );

            assertEquals(computeCommitmentParameters.getId(), computeCommitmentParameters.getOutput(), output);
        });
    }


    @Test
    public void computePhiSchnorrWithSimpleValues() {
        computePhiSchnorrTest("computePhiSchnorrSimpleValues.json");
    }

    @Test
    public void computePhiSchnorrWithRealSizeValues() {
        computePhiSchnorrTest("computePhiSchnorrRealSizeValues.json");
    }

    private void computePhiSchnorrTest(String jsonFileName) {
        List<ComputePhiSchnorrParameters> computePhiSchnorrParametersList =
                readValues(jsonFileName, ComputePhiSchnorrParameters[].class);
        computePhiSchnorrParametersList.forEach(computePhiSchnorrParameters -> {
            BigInteger output = MathHelper.computePhiSchnorr(
                    computePhiSchnorrParameters.getEg(),
                    computePhiSchnorrParameters.getX()
            );

            assertEquals(computePhiSchnorrParameters.getId(), computePhiSchnorrParameters.getOutput(), output);
        });
    }


    private <T> List<T> readValues(String jsonFileName, Class<T[]> clazz) {
        return Arrays.asList(readValue(jsonFileName, clazz));
    }

    private <T> T readValue(String jsonFileName, Class<T> clazz) {
        try {
            Path jsonPath = Paths.get(getClass().getResource("/MathHelperTest/" + jsonFileName).toURI());
            InputStream is = Files.newInputStream(jsonPath);
            ObjectMapper jsonMapper = new ObjectMapper();
            return jsonMapper.readValue(is, clazz);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Read values failed for file " + jsonFileName);
        }
    }

    private void assertEquals(String id, BigInteger expected, BigInteger actual) {
        Assert.assertTrue("Error in dataset for id : " + id, MathHelper.areEqual(expected, actual));
    }

}
