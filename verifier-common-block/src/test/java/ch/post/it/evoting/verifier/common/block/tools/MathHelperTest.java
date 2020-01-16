package ch.post.it.evoting.verifier.common.block.tools;

import ch.post.it.evoting.verifier.common.block.dto.ModExpHexParameters;
import ch.post.it.evoting.verifier.common.block.dto.ModExpParameters;
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
            BigInteger o = MathHelper.modExp(modExpParameters.getB(), modExpParameters.getE(), modExpParameters.getM());
            Assert.assertEquals("Error in dataset for id : " + modExpParameters.getId(), 0, o.compareTo(modExpParameters.getOutput()));
        });
    }

    @Test
    public void modExpWithRealSizeValues() {
        List<ModExpHexParameters> modExpParametersList = readValues("modExpRealSizeValues.json", ModExpHexParameters[].class);
        modExpParametersList.forEach(modExpHexParameters -> {
            BigInteger o = MathHelper.modExp(modExpHexParameters.getB(), modExpHexParameters.getE(), modExpHexParameters.getM());
            Assert.assertEquals("Error in dataset for id : " + modExpHexParameters.getId(), 0, o.compareTo(modExpHexParameters.getOutput()));
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
            Assert.fail("Read values failed for file " + jsonFileName);
            return null;
        }
    }

}
