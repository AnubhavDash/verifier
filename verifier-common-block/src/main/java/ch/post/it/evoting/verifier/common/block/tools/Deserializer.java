package ch.post.it.evoting.verifier.common.block.tools;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;

public class Deserializer {

    private Deserializer() {
        //private constructor, use static
    }

    public static <T> T fromJson(File inputDirectory, String filename, Class<T> targetClazz) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.readValue(getFile(inputDirectory, filename), targetClazz);
    }

    public static <T> T fromXml(File inputDirectory, String filename, Class<T> targetClazz) throws IOException {

        JacksonXmlModule jacksonXmlModule = new JacksonXmlModule();
        jacksonXmlModule.setDefaultUseWrapper(false);

        XmlMapper xmlMapper = new XmlMapper(jacksonXmlModule);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        return xmlMapper.readValue(getFile(inputDirectory, filename), targetClazz);
    }

    private static File getFile(File inputDirectory, String filename) throws FileNotFoundException {
        File[] file = inputDirectory.listFiles((dir, name) -> name.endsWith(filename));
        if (file.length == 0) {
            throw new FileNotFoundException(filename);
        } else if (file.length > 1) {
            throw new InvalidParameterException("more than one file found, filename is not specific enough");
        } else {
            return file[0];
        }
    }
}
