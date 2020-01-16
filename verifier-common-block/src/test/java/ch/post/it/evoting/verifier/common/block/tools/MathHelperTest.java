package ch.post.it.evoting.verifier.common.block.tools;

import ch.post.it.evoting.verifier.common.block.dto.ModExpHexParameters;
import ch.post.it.evoting.verifier.common.block.dto.ModExpParameters;
import ch.post.it.evoting.verifier.common.block.dto.ModExpProductHexParameters;
import ch.post.it.evoting.verifier.common.block.dto.ModExpProductParameters;
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
        List<ModExpParameters> modExpParametersList = readValues("modExpSimpleValues.json", ModExpParameters[].class);
        modExpParametersList.forEach(modExpParameters -> {
            BigInteger output = MathHelper.modExp(modExpParameters.getB(), modExpParameters.getE(), modExpParameters.getM());

            assertEquals(modExpParameters.getId(), output, modExpParameters.getOutput());
        });
    }

    @Test
    public void modExpWithRealSizeValues() {
        List<ModExpHexParameters> modExpParametersList = readValues("modExpRealSizeValues.json", ModExpHexParameters[].class);
        modExpParametersList.forEach(modExpHexParameters -> {
            BigInteger output = MathHelper.modExp(modExpHexParameters.getB(), modExpHexParameters.getE(), modExpHexParameters.getM());

            assertEquals(modExpHexParameters.getId(), output, modExpHexParameters.getOutput());
        });
    }

    @Test
    public void modExpProductWithSimpleValues() {
        List<ModExpProductParameters> modExpProductParametersList =
                readValues("modExpProductSimpleValues.json", ModExpProductParameters[].class);
        modExpProductParametersList.forEach(modExpProductParameters -> {
            BigInteger output = MathHelper.modExpProduct(
                    modExpProductParameters.getB(), modExpProductParameters.getE(), modExpProductParameters.getM());

            assertEquals(modExpProductParameters.getId(), output, modExpProductParameters.getOutput());
        });
    }

    @Test
    public void modExpProductWithRealSizeValues() {
        List<ModExpProductHexParameters> modExpProductHexParametersList =
                readValues("modExpProductRealSizeValues.json", ModExpProductHexParameters[].class);
        modExpProductHexParametersList.forEach(modExpProductHexParameters -> {
            BigInteger output = MathHelper.modExpProduct(modExpProductHexParameters.getB(),
                    modExpProductHexParameters.getE(), modExpProductHexParameters.getM());

            assertEquals(modExpProductHexParameters.getId(), output, modExpProductHexParameters.getOutput());
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
        Assert.assertEquals("Error in dataset for id : " + id, 0, expected.compareTo(actual));
    }

}
